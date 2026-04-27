package com.example.willowwallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.willowwallet.data.repository.WillowRepository
import com.example.willowwallet.utils.DateUtils


class HomeViewModel(private val repository: WillowRepository) : ViewModel() {

    // Current month boundaries — calculated once when the ViewModel is created
    private val year  = DateUtils.currentYear()
    private val month = DateUtils.currentMonth()
    private val start = DateUtils.startOfMonth(year, month)
    private val end   = DateUtils.endOfMonth(year, month)

    /** Total rands spent so far this calendar month. */
    fun getTotalSpent(userId: Int) =
        repository.getTotalSpentInPeriod(userId, start, end).asLiveData()

    /** Per-category spending totals for this month, sorted highest first. */
    fun getCategoryTotals(userId: Int) =
        repository.getCategoryTotalsInPeriod(userId, start, end).asLiveData()

    /** The saved budget goal for this month, or null if none set. */
    fun getGoal(userId: Int) =
        repository.observeGoalForMonth(userId, year, month).asLiveData()


    fun getRecentExpenses(userId: Int) =
        repository.getRecentExpenses(userId).asLiveData()


    fun calcProgressPct(totalSpent: Double, maxGoal: Double): Int {
        if (maxGoal <= 0.0) return 0
        return ((totalSpent / maxGoal) * 100.0).toInt().coerceIn(0, 100)
    }


    fun budgetStatus(totalSpent: Double, minGoal: Double, maxGoal: Double): String = when {
        totalSpent > maxGoal  -> "over"
        totalSpent >= minGoal -> "on_track"
        else                  -> "under"
    }
}