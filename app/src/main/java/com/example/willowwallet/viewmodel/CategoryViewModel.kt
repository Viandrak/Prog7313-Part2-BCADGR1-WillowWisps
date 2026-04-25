package com.example.willowwallet.viewmodel
import androidx.lifecycle.*
import com.example.willowwallet.data.entities.Category
import com.example.willowwallet.data.repository.WillowRepository
import kotlinx.coroutines.launch

class CategoryViewModel(private val repository: WillowRepository) : ViewModel() {

    private val _userId = MutableLiveData<Int>()
    val categories: LiveData<List<Category>> = _userId.switchMap {
        repository.getCategoriesForUser(it).asLiveData()
    }

    private val _saveResult = MutableLiveData<Result<Unit>?>()
    val saveResult: LiveData<Result<Unit>?> = _saveResult

    private val _deleteResult = MutableLiveData<Result<Unit>?>()
    val deleteResult: LiveData<Result<Unit>?> = _deleteResult

    fun setUserId(userId: Int) { if (_userId.value != userId) _userId.value = userId }

    fun saveCategory(userId: Int, name: String, colorHex: String, icon: String, budgetLimit: Double, existingId: Int = 0) {
        if (name.isBlank()) { _saveResult.value = Result.failure(Exception("Category name cannot be empty")); return }
        viewModelScope.launch {
            try {
                if (repository.isCategoryNameDuplicate(userId, name.trim(), existingId)) {
                    _saveResult.postValue(Result.failure(Exception("Category '${name.trim()}' already exists")))
                    return@launch
                }
                repository.saveCategory(Category(id = existingId, userId = userId, name = name.trim(), colorHex = colorHex, icon = icon, budgetLimit = budgetLimit))
                _saveResult.postValue(Result.success(Unit))
            } catch (e: Exception) { _saveResult.postValue(Result.failure(e)) }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try { repository.deleteCategory(category); _deleteResult.postValue(Result.success(Unit)) }
            catch (e: Exception) { _deleteResult.postValue(Result.failure(e)) }
        }
    }

    fun resetSaveResult() { _saveResult.value = null }
    fun resetDeleteResult() { _deleteResult.value = null }
}