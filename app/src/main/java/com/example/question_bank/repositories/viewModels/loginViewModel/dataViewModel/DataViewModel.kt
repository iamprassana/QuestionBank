package com.example.question_bank.repositories.viewModels.loginViewModel.dataViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.question_bank.data.User
import com.example.question_bank.repositories.FireBaseProvider
import com.example.question_bank.repositories.fireStore.FireStoreClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DataViewModel() : ViewModel(){
    private val auth = FireBaseProvider.auth
    private val fireStore = FireStoreClient()
    private val _user = MutableStateFlow<User?>(null)
    val user : StateFlow<User?> = _user

    init {
        loadUserData()
    }

    fun loadUserData() {
        val currentUserId = auth.currentUser?.uid
        if(currentUserId != null) {
            viewModelScope.launch {
                fireStore.getUserRealtime(userId = currentUserId).collect { user ->
                    _user.value = user
                    Log.i("User Data" , user.toString())
                }
            }
        }
    }

    fun clearData() {
        _user.value = null
    }
}