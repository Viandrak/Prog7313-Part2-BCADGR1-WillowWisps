package com.example.willowwallet.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.example.willowwallet.data.entities.Category
import com.example.willowwallet.data.entities.Expense
import com.example.willowwallet.data.entities.ExpenseWithCategory
import com.example.willowwallet.data.repository.WillowRepository
import com.example.willowwallet.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar

class ExpenseViewModel(private val repository: WillowRepository) : ViewModel() {

    private val _saveResult = MutableLiveData<Result<Unit>>()
    val saveResult: LiveData<Result<Unit>> = _saveResult

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _photoUri = MutableLiveData<Uri?>()
    val photoUri: LiveData<Uri?> = _photoUri

    private val _startDate = MutableLiveData<Long>()
    private val _endDate   = MutableLiveData<Long>()
    val startDate: LiveData<Long> = _startDate
    val endDate:   LiveData<Long> = _endDate

    init {
        val now = System.currentTimeMillis()
        _startDate.value = DateUtils.startOfDay(
            Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }.timeInMillis
        )
        _endDate.value = DateUtils.endOfDay(now)
    }

    fun loadCategories(userId: Int) {
        viewModelScope.launch {
            val list = repository.getCategoriesOnce(userId)
            _categories.value = list
        }
    }

    fun setPhotoUri(uri: Uri?) { _photoUri.value = uri }
    fun clearPhoto()           { _photoUri.value = null }

    fun saveExpense(
        userId:      Int,
        amount:      Double,
        date:        Long,
        startTime:   String,
        endTime:     String,
        description: String,
        categoryId:  Int?,
        photoPath:   String?
    ) {
        // Validation
        if (amount <= 0) {
            _saveResult.value = Result.failure(Exception("Amount must be greater than zero"))
            return
        }
        if (description.isBlank()) {
            _saveResult.value = Result.failure(Exception("Description cannot be empty"))
            return
        }
        if (!DateUtils.isValidTimeFormat(startTime)) {
            _saveResult.value = Result.failure(Exception("Invalid start time format (HH:MM)"))
            return
        }
        if (!DateUtils.isValidTimeFormat(endTime)) {
            _saveResult.value = Result.failure(Exception("Invalid end time format (HH:MM)"))
            return
        }
        if (startTime >= endTime) {
            _saveResult.value = Result.failure(Exception("End time must be after start time"))
            return
        }

        viewModelScope.launch {
            try {
                repository.saveExpense(
                    Expense(
                        userId      = userId,
                        amount      = amount,
                        date        = date,
                        startTime   = startTime,
                        endTime     = endTime,
                        description = description,
                        categoryId  = categoryId,
                        photoPath   = photoPath
                    )
                )
                _saveResult.value = Result.success(Unit)
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
            }
        }
    }

    fun setDateRange(start: Long, end: Long) {
        _startDate.value = DateUtils.startOfDay(start)
        _endDate.value   = DateUtils.endOfDay(end)
    }

    fun getExpensesInPeriod(userId: Int): Flow<List<ExpenseWithCategory>> =
        repository.getExpensesInPeriod(
            userId,
            _startDate.value ?: 0L,
            _endDate.value   ?: System.currentTimeMillis()
        )
}