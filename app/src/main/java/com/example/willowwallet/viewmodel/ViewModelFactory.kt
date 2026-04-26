package com.example.willowwallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.willowwallet.data.repository.WillowRepository

class ViewModelFactory(private val repository: WillowRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AuthViewModel::class.java)     -> AuthViewModel(repository)     as T
        modelClass.isAssignableFrom(CategoryViewModel::class.java) -> CategoryViewModel(repository) as T
        modelClass.isAssignableFrom(ExpenseViewModel::class.java)  -> ExpenseViewModel(repository)  as T
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}