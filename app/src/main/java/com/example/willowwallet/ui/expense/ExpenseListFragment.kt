package com.example.willowwallet.ui.expense

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.willowwallet.data.db.WillowDatabase
import com.example.willowwallet.data.repository.WillowRepository
import com.example.willowwallet.databinding.FragmentExpenseListBinding
import com.example.willowwallet.utils.DateUtils
import com.example.willowwallet.utils.SessionManager
import com.example.willowwallet.viewmodel.ExpenseViewModel
import com.example.willowwallet.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class ExpenseListFragment : Fragment() {

    private var _binding: FragmentExpenseListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExpenseViewModel by viewModels {
        val db   = WillowDatabase.getInstance(requireContext())
        val repo = WillowRepository(db.userDao(), db.categoryDao(), db.expenseDao(), db.budgetGoalDao())
        ViewModelFactory(repo)
    }

    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        setupRecyclerView()
        setupDateFilters()
        setupFab()
        observeDateRange()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        adapter = ExpenseAdapter { expenseWithCategory ->
            val path = expenseWithCategory.expense.photoPath
            if (!path.isNullOrBlank()) {
                val intent = Intent(requireContext(), FullscreenPhotoActivity::class.java)
                intent.putExtra(FullscreenPhotoActivity.EXTRA_PHOTO_PATH, path)
                startActivity(intent)
            }
        }
        binding.recyclerExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerExpenses.adapter = adapter
    }

    private fun setupDateFilters() {
        binding.btnStartDate.setOnClickListener {
            val cal = Calendar.getInstance().apply {
                timeInMillis = viewModel.startDate.value ?: System.currentTimeMillis()
            }
            DatePickerDialog(requireContext(), { _, y, m, d ->
                cal.set(y, m, d)
                val newStart = cal.timeInMillis
                val end      = viewModel.endDate.value ?: System.currentTimeMillis()
                viewModel.setDateRange(newStart, if (newStart <= end) end else newStart)
                loadExpenses()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.btnEndDate.setOnClickListener {
            val cal = Calendar.getInstance().apply {
                timeInMillis = viewModel.endDate.value ?: System.currentTimeMillis()
            }
            DatePickerDialog(requireContext(), { _, y, m, d ->
                cal.set(y, m, d)
                val newEnd = cal.timeInMillis
                val start  = viewModel.startDate.value ?: 0L
                viewModel.setDateRange(if (newEnd >= start) start else newEnd, newEnd)
                loadExpenses()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupFab() {
        binding.fabAddExpense.setOnClickListener {
            startActivity(Intent(requireContext(), AddExpenseActivity::class.java))
        }
    }

    private fun observeDateRange() {
        viewModel.startDate.observe(viewLifecycleOwner) { start ->
            binding.tvStartDate.text = DateUtils.formatDate(start)
            loadExpenses()
        }
        viewModel.endDate.observe(viewLifecycleOwner) { end ->
            binding.tvEndDate.text = DateUtils.formatDate(end)
        }
    }

    private fun loadExpenses() {
        val userId = sessionManager.getUserId()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getExpensesInPeriod(userId).collectLatest { list ->
                adapter.submitList(list)
                binding.tvEmpty.visibility      = if (list.isEmpty()) View.VISIBLE else View.GONE
                binding.recyclerExpenses.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
                binding.tvTotal.text = "Total: ${DateUtils.formatCurrency(list.sumOf { it.expense.amount })}"
            }
        }
    }
}