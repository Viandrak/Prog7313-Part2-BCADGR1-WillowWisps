package com.example.willowwallet.ui.expense

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.willowwallet.data.db.WillowDatabase
import com.example.willowwallet.data.entities.Category
import com.example.willowwallet.data.repository.WillowRepository
import com.example.willowwallet.databinding.ActivityAddExpenseBinding
import com.example.willowwallet.utils.DateUtils
import com.example.willowwallet.utils.SessionManager
import com.example.willowwallet.viewmodel.ExpenseViewModel
import com.example.willowwallet.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import com.bumptech.glide.Glide

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding

    private val viewModel: ExpenseViewModel by viewModels {
        val db   = WillowDatabase.getInstance(applicationContext)
        val repo = WillowRepository(db.userDao(), db.categoryDao(), db.expenseDao(), db.budgetGoalDao())
        ViewModelFactory(repo)
    }

    private lateinit var sessionManager: SessionManager

    private var selectedDateMillis: Long = System.currentTimeMillis()
    private var selectedCategoryId: Int? = null
    private var categoryList:       List<Category> = emptyList()
    private var tempCameraUri:      Uri? = null
    private var currentPhotoPath:   String? = null


    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) launchCamera() else showSnack("Camera permission denied")
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) viewModel.setPhotoUri(tempCameraUri)
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Take persistent permission so URI stays valid
            try {
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Some URIs don't support persistable permissions, that's fine
            }
            // Copy to private storage immediately so we have a stable file path
            val savedPath = copyUriToPrivateStorage(uri)
            currentPhotoPath = savedPath
            // Load from the saved file path instead of the URI to avoid black screen
            if (savedPath != null) {
                val fileUri = Uri.fromFile(File(savedPath))
                viewModel.setPhotoUri(fileUri)
            } else {
                viewModel.setPhotoUri(uri)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Add Expense"
            setDisplayHomeAsUpEnabled(true)
        }

        sessionManager = SessionManager(this)
        setupDateField()
        setupTimeFields()
        setupPhotoButton()
        setupSaveButton()
        setupObservers()

        viewModel.loadCategories(sessionManager.getUserId())
        binding.etDate.setText(DateUtils.formatDate(selectedDateMillis))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }


    private fun setupDateField() {
        binding.etDate.isFocusable = false
        binding.etDate.setOnClickListener {
            val cal = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
            DatePickerDialog(this, { _, y, m, d ->
                cal.set(y, m, d)
                selectedDateMillis = cal.timeInMillis
                binding.etDate.setText(DateUtils.formatDate(selectedDateMillis))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupTimeFields() {
        binding.etStartTime.isFocusable = false
        binding.etEndTime.isFocusable   = false
        binding.etStartTime.setOnClickListener { showTimePicker(isStart = true) }
        binding.etEndTime.setOnClickListener   { showTimePicker(isStart = false) }
    }

    private fun showTimePicker(isStart: Boolean) {
        val cal = Calendar.getInstance()
        TimePickerDialog(this, { _, hour, minute ->
            val t = String.format("%02d:%02d", hour, minute)
            if (isStart) binding.etStartTime.setText(t) else binding.etEndTime.setText(t)
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }


    private fun setupCategorySpinner(categories: List<Category>) {
        categoryList = categories
        val names = mutableListOf("No Category") + categories.map { "${it.icon} ${it.name}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, names)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                selectedCategoryId = if (pos == 0) null else categories[pos - 1].id
            }
            override fun onNothingSelected(p: AdapterView<*>?) { selectedCategoryId = null }
        }
    }

    private fun setupPhotoButton() {
        binding.btnAddPhoto.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Add Photo")
                .setItems(arrayOf("Take Photo", "Choose from Gallery")) { _, which ->
                    if (which == 0) checkCameraAndLaunch() else galleryLauncher.launch("image/*")
                }.show()
        }
        binding.btnRemovePhoto.setOnClickListener {
            viewModel.clearPhoto()
            currentPhotoPath = null
        }
    }

    private fun checkCameraAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) launchCamera()
        else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun launchCamera() {
        val file         = createImageFile()
        tempCameraUri    = FileProvider.getUriForFile(this, "${packageName}.provider", file)
        currentPhotoPath = file.absolutePath
        cameraLauncher.launch(tempCameraUri!!)
    }

    private fun createImageFile(): File {
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File.createTempFile("EXPENSE_${stamp}_", ".jpg",
            getExternalFilesDir(Environment.DIRECTORY_PICTURES))
    }

    private fun copyUriToPrivateStorage(uri: Uri): String? {
        return try {
            val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file  = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "EXPENSE_${stamp}.jpg")
            contentResolver.openInputStream(uri)?.use { it.copyTo(file.outputStream()) }
            file.absolutePath
        } catch (e: Exception) { null }
    }


    private fun setupObservers() {
        viewModel.categories.observe(this) { setupCategorySpinner(it) }

        viewModel.photoUri.observe(this) { uri ->
            if (uri != null && currentPhotoPath != null) {
                binding.ivPhotoPreview.visibility = View.VISIBLE
                binding.btnRemovePhoto.visibility = View.VISIBLE
                binding.btnAddPhoto.text          = "Change Photo"

                Glide.with(this)
                    .load(File(currentPhotoPath!!))
                    .centerCrop()
                    .into(binding.ivPhotoPreview)
            } else {
                binding.ivPhotoPreview.visibility = View.GONE
                binding.btnRemovePhoto.visibility = View.GONE
                binding.btnAddPhoto.text          = "Add Photo"
            }
        }

        viewModel.saveResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE
            binding.btnSave.isEnabled      = true
            result.fold(
                onSuccess = {
                    Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                },
                onFailure = { showSnack(it.message ?: "Failed to save expense") }
            )
        }
    }


    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            val amount = binding.etAmount.text.toString().trim().toDoubleOrNull()
            if (amount == null) { showSnack("Enter a valid amount"); return@setOnClickListener }

            val description = binding.etDescription.text.toString().trim()
            val startTime   = binding.etStartTime.text.toString().trim()
            val endTime     = binding.etEndTime.text.toString().trim()

            if (startTime.isEmpty()) { showSnack("Select a start time"); return@setOnClickListener }
            if (endTime.isEmpty())   { showSnack("Select an end time");   return@setOnClickListener }

            binding.progressBar.visibility = View.VISIBLE
            binding.btnSave.isEnabled      = false

            viewModel.saveExpense(
                userId      = sessionManager.getUserId(),
                amount      = amount,
                date        = selectedDateMillis,
                startTime   = startTime,
                endTime     = endTime,
                description = description,
                categoryId  = selectedCategoryId,
                photoPath   = currentPhotoPath
            )
        }
    }

    private fun showSnack(msg: String) =
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
}