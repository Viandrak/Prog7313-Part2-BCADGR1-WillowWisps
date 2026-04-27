package com.example.willowwallet.ui.home

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.willowwallet.MainActivity
import com.example.willowwallet.R
import com.example.willowwallet.ui.expense.AddExpenseActivity
import com.example.willowwallet.utils.DateUtils
import com.example.willowwallet.viewmodel.HomeViewModel
import com.example.willowwallet.viewmodel.ViewModelFactory
import com.google.android.material.button.MaterialButton


class HomeFragment : Fragment() {

    private lateinit var vm: HomeViewModel

    private lateinit var tvGreeting:     TextView
    private lateinit var tvTotalSpent:   TextView
    private lateinit var tvBudgetStatus: TextView
    private lateinit var tvBudgetRange:  TextView
    private lateinit var progressBudget: ProgressBar
    private lateinit var tvNoBudget:     TextView
    private lateinit var rvCategories:   RecyclerView
    private lateinit var rvRecent:       RecyclerView
    private lateinit var tvNoRecent:     TextView
    private lateinit var btnAddExpense:  MaterialButton

    private lateinit var categoryAdapter: CategoryProgressAdapter
    private lateinit var recentAdapter:   RecentExpensesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setupRecyclerViews()

        val activity    = requireActivity() as MainActivity
        val factory     = ViewModelFactory(activity.repository)
        vm              = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        val userId      = activity.sessionManager.getUserId()
        val displayName = activity.sessionManager.getDisplayName()

        android.util.Log.d("HomeFragment", "userId=$userId name=$displayName")

        tvGreeting.text = "Good day, ${displayName.ifBlank { "there" }} \uD83D\uDC4B"

        btnAddExpense.setOnClickListener {
            startActivity(Intent(requireContext(), AddExpenseActivity::class.java))
        }

        observeData(userId)
    }

    private fun bindViews(v: View) {
        tvGreeting     = v.findViewById(R.id.tv_greeting)
        tvTotalSpent   = v.findViewById(R.id.tv_total_spent)
        tvBudgetStatus = v.findViewById(R.id.tv_budget_status)
        tvBudgetRange  = v.findViewById(R.id.tv_budget_range)
        progressBudget = v.findViewById(R.id.progress_budget)
        tvNoBudget     = v.findViewById(R.id.tv_no_budget)
        rvCategories   = v.findViewById(R.id.rv_category_progress)
        rvRecent       = v.findViewById(R.id.rv_recent_expenses)
        tvNoRecent     = v.findViewById(R.id.tv_no_recent)
        btnAddExpense  = v.findViewById(R.id.btn_quick_add)
    }

    private fun setupRecyclerViews() {
        categoryAdapter = CategoryProgressAdapter()
        rvCategories.layoutManager = LinearLayoutManager(requireContext())
        rvCategories.adapter = categoryAdapter
        rvCategories.isNestedScrollingEnabled = false

        recentAdapter = RecentExpensesAdapter()
        rvRecent.layoutManager = LinearLayoutManager(requireContext())
        rvRecent.adapter = recentAdapter
        rvRecent.isNestedScrollingEnabled = false
    }

    private fun observeData(userId: Int) {

        // Total spent this month
        vm.getTotalSpent(userId).observe(viewLifecycleOwner) { total ->
            tvTotalSpent.text = DateUtils.formatCurrency(total)
        }

        // Budget goal drives the progress bar and status label
        vm.getGoal(userId).observe(viewLifecycleOwner) { goal ->
            if (goal == null) {
                tvNoBudget.visibility     = View.VISIBLE
                progressBudget.visibility = View.INVISIBLE
                tvBudgetStatus.text       = "No budget set"
                tvBudgetStatus.setTextColor(Color.parseColor("#4d6580"))
                tvBudgetRange.text        = "Set one in the Goals tab"
            } else {
                tvNoBudget.visibility     = View.GONE
                progressBudget.visibility = View.VISIBLE
                tvBudgetRange.text =
                    "${DateUtils.formatCurrency(goal.minimumGoal)} \u2013 ${DateUtils.formatCurrency(goal.maximumGoal)}"

                vm.getTotalSpent(userId).observe(viewLifecycleOwner) { total ->
                    val pct    = vm.calcProgressPct(total, goal.maximumGoal)
                    val status = vm.budgetStatus(total, goal.minimumGoal, goal.maximumGoal)

                    progressBudget.progress = pct
                    val barColor = if (status == "over") Color.parseColor("#f87171")
                    else                  Color.parseColor("#2dd4bf")
                    progressBudget.progressTintList = ColorStateList.valueOf(barColor)

                    tvBudgetStatus.text = when (status) {
                        "over"     -> "\u26A0\uFE0F Over budget!"
                        "on_track" -> "\u2705 On track"
                        else       -> "\uD83D\uDCB0 Under budget"
                    }
                    tvBudgetStatus.setTextColor(
                        if (status == "over") Color.parseColor("#f87171")
                        else                  Color.parseColor("#4ade80")
                    )
                }
            }
        }

        // Category mini-bars
        vm.getCategoryTotals(userId).observe(viewLifecycleOwner) { cats ->
            categoryAdapter.submitList(cats ?: emptyList())
        }

        // Recent expenses
        vm.getRecentExpenses(userId).observe(viewLifecycleOwner) { list ->
            val expenses = list ?: emptyList()
            tvNoRecent.visibility = if (expenses.isEmpty()) View.VISIBLE else View.GONE
            rvRecent.visibility   = if (expenses.isEmpty()) View.GONE   else View.VISIBLE
            recentAdapter.submitList(expenses)
        }
    }
}