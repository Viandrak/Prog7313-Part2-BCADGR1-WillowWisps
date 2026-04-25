package com.example.willowwallet.viewmodel
import androidx.lifecycle.*
import com.example.willowwallet.data.entities.User
import com.example.willowwallet.data.repository.WillowRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: WillowRepository) : ViewModel() {

    sealed class AuthResult {
        data class Success(val user: User) : AuthResult()
        data class Error(val message: String) : AuthResult()
        object Loading : AuthResult()
    }

    private val _authResult = MutableLiveData<AuthResult?>()
    val authResult: LiveData<AuthResult?> = _authResult

    fun login(username: String, password: String) {
        if (username.isBlank()) { _authResult.value = AuthResult.Error("Please enter your username"); return }
        if (password.isBlank()) { _authResult.value = AuthResult.Error("Please enter your password"); return }
        _authResult.value = AuthResult.Loading
        viewModelScope.launch {
            val user = repository.login(username.trim(), password)
            _authResult.postValue(if (user != null) AuthResult.Success(user) else AuthResult.Error("Invalid username or password"))
        }
    }

    fun register(username: String, password: String, confirmPassword: String, displayName: String) {
        if (username.isBlank()) { _authResult.value = AuthResult.Error("Username cannot be empty"); return }
        if (username.length < 3) { _authResult.value = AuthResult.Error("Username must be at least 3 characters"); return }
        if (password.length < 6) { _authResult.value = AuthResult.Error("Password must be at least 6 characters"); return }
        if (password != confirmPassword) { _authResult.value = AuthResult.Error("Passwords do not match"); return }
        _authResult.value = AuthResult.Loading
        viewModelScope.launch {
            val name = displayName.trim().ifBlank { username.trim() }
            val userId = repository.registerUser(username.trim(), password, name)
            if (userId > 0) {
                val user = repository.getUserById(userId.toInt())
                _authResult.postValue(if (user != null) AuthResult.Success(user) else AuthResult.Error("Registration failed"))
            } else {
                _authResult.postValue(AuthResult.Error("Username already taken"))
            }
        }
    }

    fun resetResult() { _authResult.value = null }
}