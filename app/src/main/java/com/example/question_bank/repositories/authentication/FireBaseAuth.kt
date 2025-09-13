package com.example.question_bank.repositories.authentication

import com.example.question_bank.repositories.FireBaseProvider
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.tasks.await

class FirebaseAuth() {
    private val auth = FireBaseProvider.auth

    fun currentUser() = auth.currentUser

    suspend fun login(email : String, password : String, route : String) : AuthResult {
        return auth.signInWithEmailAndPassword(email, password).await()
        }

    suspend fun register(name : String, email : String, password : String) : AuthResult {
        return auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun logout() {
        auth.signOut()
    }

}