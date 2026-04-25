package com.example.willowwallet.ui.category

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.willowwallet.data.db.WillowDatabase
import com.example.willowwallet.data.repository.WillowRepository
import com.example.willowwallet.databinding.ActivityAddCategoryBinding
import com.example.willowwallet.utils.SessionManager
import com.example.willowwallet.viewmodel.CategoryViewModel
import com.example.willowwallet.viewmodel.ViewModelFactory

class AddCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCategoryBinding
    private lateinit var viewModel: CategoryViewModel
    private lateinit var session: SessionManager

    private var selectedColor = "#2DD4BF"
    private var selectedIcon = "🛒"
    private var existingId = 0

    private val icons = listOf(
        "🛒", "✈️", "🎵", "📺",
        "🐾", "🎸", "🏆", "💄",
        "🍕", "🏋️", "💊", "🎮"
    )

    private val colors = listOf(
        "#2DD4BF", "#A78BFA", "#F59E0B", "#4ADE80",
        "#F87171", "#60A5FA", "#FB923C", "#EC4899",
        "#34D399", "#FBBF24", "#818CF8"
    )

    companion object {
        const val EXTRA_CATEGORY_ID = "extra_category_id"
        const val EXTRA_CATEGORY_NAME = "extra_category_name"
        const val EXTRA_CATEGORY_ICON = "extra_category_icon"
        const val EXTRA_CATEGORY_COLOR = "extra_category_color"
        const val EXTRA_CATEGORY_BUDGET = "extra_category_budget"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        val db = WillowDatabase.getInstance(this)
        val repo = WillowRepository(db.userDao(), db.categoryDao(), db.expenseDao(), db.budgetGoalDao())
        viewModel = ViewModelProvider(this, ViewModelFactory(repo))[CategoryViewModel::class.java]
        viewModel.setUserId(session.getUserId())

        existingId = intent.getIntExtra(EXTRA_CATEGORY_ID, 0)
        if (existingId != 0) {
            binding.etCategoryName.setText(intent.getStringExtra(EXTRA_CATEGORY_NAME) ?: "")
            val budget = intent.getDoubleExtra(EXTRA_CATEGORY_BUDGET, 0.0)
            if (budget > 0) binding.etBudget.setText(budget.toInt().toString())
            selectedIcon = intent.getStringExtra(EXTRA_CATEGORY_ICON) ?: "🛒"
            selectedColor = intent.getStringExtra(EXTRA_CATEGORY_COLOR) ?: "#2DD4BF"
            binding.tvTitle.text = "Edit Category"
        }

        setupIconGrid()
        setupColorGrid()

        binding.btnBack.setOnClickListener { finish() }

        binding.btnSaveCategory.setOnClickListener {
            val name = binding.etCategoryName.text.toString()
            val budget = binding.etBudget.text.toString().toDoubleOrNull() ?: 0.0
            viewModel.saveCategory(session.getUserId(), name, selectedColor, selectedIcon, budget, existingId)
        }

        viewModel.saveResult.observe(this) { result ->
            result ?: return@observe
            if (result.isSuccess) {
                Toast.makeText(this, if (existingId == 0) "Category saved!" else "Category updated!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, result.exceptionOrNull()?.message ?: "Error", Toast.LENGTH_LONG).show()
            }
            viewModel.resetSaveResult()
        }
    }

    private fun setupIconGrid() {
        val iconViews = listOf(
            binding.icon1, binding.icon2, binding.icon3, binding.icon4,
            binding.icon5, binding.icon6, binding.icon7, binding.icon8,
            binding.icon9, binding.icon10, binding.icon11, binding.icon12
        )
        iconViews.forEachIndexed { index, textView ->
            if (index < icons.size) {
                textView.text = icons[index]
                textView.setOnClickListener {
                    selectedIcon = icons[index]
                    iconViews.forEach { it.setBackgroundResource(com.example.willowwallet.R.drawable.bg_card_dark) }
                    textView.setBackgroundResource(com.example.willowwallet.R.drawable.bg_icon_selected)
                }
                if (icons[index] == selectedIcon) {
                    textView.setBackgroundResource(com.example.willowwallet.R.drawable.bg_icon_selected)
                } else {
                    textView.setBackgroundResource(com.example.willowwallet.R.drawable.bg_card_dark)
                }
            }
        }
    }

    private fun setupColorGrid() {
        val colorViews = listOf(
            binding.color1, binding.color2, binding.color3, binding.color4,
            binding.color5, binding.color6, binding.color7, binding.color8,
            binding.color9, binding.color10, binding.color11
        )
        colorViews.forEachIndexed { index, view ->
            if (index < colors.size) {
                view.setBackgroundColor(Color.parseColor(colors[index]))
                view.clipToOutline = true
                view.outlineProvider = object : android.view.ViewOutlineProvider() {
                    override fun getOutline(view: android.view.View, outline: android.graphics.Outline) {
                        outline.setOval(0, 0, view.width, view.height)
                    }
                }
                view.setOnClickListener {
                    selectedColor = colors[index]
                    colorViews.forEach { it.scaleX = 1f; it.scaleY = 1f }
                    view.scaleX = 1.3f
                    view.scaleY = 1.3f
                }
                if (colors[index] == selectedColor) {
                    view.scaleX = 1.3f
                    view.scaleY = 1.3f
                }
            }
        }
    }
}