package com.example.willowwallet.ui.category

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.willowwallet.MainActivity
import com.example.willowwallet.data.entities.Category
import com.example.willowwallet.databinding.FragmentCategoriesBinding
import com.example.willowwallet.utils.SessionManager
import com.example.willowwallet.viewmodel.CategoryViewModel
import com.example.willowwallet.viewmodel.ViewModelFactory

class CategoriesFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CategoryViewModel
    private lateinit var session: SessionManager
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = requireActivity() as MainActivity
        session = mainActivity.sessionManager
        viewModel = ViewModelProvider(this, ViewModelFactory(mainActivity.repository))[CategoryViewModel::class.java]
        viewModel.setUserId(session.getUserId())

        adapter = CategoryAdapter(
            emptyList(),
            onEdit = { openEditScreen(it) },
            onDelete = { confirmDelete(it) }
        )
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = adapter

        viewModel.categories.observe(viewLifecycleOwner) { cats ->
            adapter.updateData(cats)
            binding.tvEmpty.visibility = if (cats.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            Toast.makeText(requireContext(), "Category deleted", Toast.LENGTH_SHORT).show()
            viewModel.resetDeleteResult()
        }

        binding.fabAddCategory.setOnClickListener {
            startActivity(Intent(requireContext(), AddCategoryActivity::class.java))
        }
    }

    private fun openEditScreen(existing: Category) {
        val intent = Intent(requireContext(), AddCategoryActivity::class.java).apply {
            putExtra(AddCategoryActivity.EXTRA_CATEGORY_ID, existing.id)
            putExtra(AddCategoryActivity.EXTRA_CATEGORY_NAME, existing.name)
            putExtra(AddCategoryActivity.EXTRA_CATEGORY_ICON, existing.icon)
            putExtra(AddCategoryActivity.EXTRA_CATEGORY_COLOR, existing.colorHex)
            putExtra(AddCategoryActivity.EXTRA_CATEGORY_BUDGET, existing.budgetLimit)
        }
        startActivity(intent)
    }

    private fun confirmDelete(category: Category) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(com.example.willowwallet.R.layout.dialog_delete_confirm, null)

        val tvMessage = dialogView.findViewById<android.widget.TextView>(com.example.willowwallet.R.id.tv_delete_message)
        tvMessage.text = "Delete '${category.name}'?\n\nExpenses in this category will become uncategorized."

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<android.widget.TextView>(com.example.willowwallet.R.id.btn_cancel_delete).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<android.widget.TextView>(com.example.willowwallet.R.id.btn_confirm_delete).setOnClickListener {
            viewModel.deleteCategory(category)
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setUserId(session.getUserId())
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}