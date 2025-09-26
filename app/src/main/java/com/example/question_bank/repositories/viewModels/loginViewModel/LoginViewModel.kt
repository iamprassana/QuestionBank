package com.example.question_bank.repositories.viewModels.loginViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.question_bank.repositories.authentication.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _showPassword = MutableStateFlow(false)
    val showPassword: StateFlow<Boolean> = _showPassword

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun setUserName(name: String) {
        _email.value = name
    }

    fun setPassword(passWord: String) {
        _password.value = passWord
    }

    fun toggleShowPassword() {
        _showPassword.value = !_showPassword.value
    }
    
    fun resetPassWord(auth : FirebaseAuth, email : String , onResult : (Boolean, String?) -> Unit){
        if(email.isEmpty()) {
            _errorMessage.value = "Email cannot be null"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = auth.resetPassword(email.trim())
            if(result.isSuccess) {
                onResult(true, null)
            }else {
                _errorMessage.value = result.exceptionOrNull()?.message
                onResult(false, result.exceptionOrNull()?.message)
            }
        }

        _isLoading.value =  false
    }

    fun login(auth: FirebaseAuth, onSuccess: () -> Unit) {
        if (_email.value.isEmpty() || _password.value.isEmpty()) {
            _errorMessage.value = "Email or Password cannot be empty"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = auth.login(_email.value, _password.value, "home")
                val user = result.user
                if (user != null) {
                    onSuccess()
                } else {
                    _errorMessage.value = "Login failed"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Wrong Password. Please enter correct password"
                _password.value = ""   // clear password on wrong entry
                _showPassword.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetError() {
        _errorMessage.value = null
    }
}
