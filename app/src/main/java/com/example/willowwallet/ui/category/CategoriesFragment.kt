package com.example.willowwallet.ui.category

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.willowwallet.MainActivity
import com.example.willowwallet.R
import com.example.willowwallet.data.entities.Category
import com.example.willowwallet.databinding.FragmentCategoriesBinding
import com.example.willowwallet.viewmodel.CategoryViewModel
import com.example.willowwallet.viewmodel.ViewModelFactory

class CategoriesFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val mainActivity = requireActivity() as MainActivity
            val userId = mainActivity.sessionManager.getUserId()

            viewModel = ViewModelProvider(this, ViewModelFactory(mainActivity.repository))[CategoryViewModel::class.java]
            viewModel.setUserId(userId)

            adapter = CategoryAdapter(
                emptyList(),
                onEdit = { openEditScreen(it) },
                onDelete = { confirmDelete(it) }
            )
            binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
            binding.rvCategories.adapter = adapter

            viewModel.categories.observe(viewLifecycleOwner) { cats ->
                val categoryList = cats ?: emptyList()
                adapter.updateData(categoryList)
                binding.tvEmpty.visibility = if (categoryList.isEmpty()) View.VISIBLE else View.GONE
            }

            viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
                result ?: return@observe
                Toast.makeText(requireContext(), "Category deleted", Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteResult()
            }

            binding.fabAddCategory.setOnClickListener {
                startActivity(Intent(requireContext(), AddCategoryActivity::class.java))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvEmpty.text = "Error: ${e.message}"
            binding.tvEmpty.visibility = View.VISIBLE
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
        try {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_delete_confirm, null)

            val tvMessage = dialogView.findViewById<TextView>(R.id.tv_delete_message)
            tvMessage.text = "Delete '${category.name}'?\n\nExpenses in this category will become uncategorized."

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            dialogView.findViewById<TextView>(R.id.btn_cancel_delete).setOnClickListener {
                dialog.dismiss()
            }

            dialogView.findViewById<TextView>(R.id.btn_confirm_delete).setOnClickListener {
                viewModel.deleteCategory(category)
                dialog.dismiss()
            }

            dialog.show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}