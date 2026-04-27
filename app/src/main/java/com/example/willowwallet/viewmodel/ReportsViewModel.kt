package com.example.willowwallet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.example.willowwallet.data.entities.CategoryTotal
import com.example.willowwallet.data.repository.WillowRepository
import com.example.willowwallet.utils.DateUtils


class ReportsViewModel(private val repository: WillowRepository) : ViewModel() {

    private var userId = -1

    private val _dateRange = MutableLiveData<Pair<Long, Long>>()
    val dateRange: LiveData<Pair<Long, Long>> = _dateRange

    val categoryTotals: LiveData<List<CategoryTotal>> = _dateRange.switchMap { (start, end) ->
        if (userId == -1) return@switchMap MutableLiveData(emptyList())
        repository.getCategoryTotalsInPeriod(userId, start, end).asLiveData()
    }

    val totalSpent: LiveData<Double> = _dateRange.switchMap { (start, end) ->
        if (userId == -1) return@switchMap MutableLiveData(0.0)
        repository.getTotalSpentInPeriod(userId, start, end).asLiveData()
    }


    fun init(uid: Int) {
        userId = uid
        android.util.Log.d("ReportsViewModel", "init userId=$userId")
        setCurrentMonth()
    }

    fun setCurrentMonth() {
        val y = DateUtils.currentYear()
        val m = DateUtils.currentMonth()
        _dateRange.value = Pair(DateUtils.startOfMonth(y, m), DateUtils.endOfMonth(y, m))
    }


    fun setCustomRange(startMillis: Long, endMillis: Long): Boolean {
        if (startMillis > endMillis) {
            android.util.Log.w("ReportsViewModel", "Rejected: start > end")
            return false
        }
        _dateRange.value = Pair(
            DateUtils.startOfDay(startMillis),
            DateUtils.endOfDay(endMillis)
        )
        return true
    }
}