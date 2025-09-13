package com.example.question_bank.repositories.fireStore

import com.example.question_bank.data.User
import com.example.question_bank.repositories.FireBaseProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FireStoreClient {

    private val db = FireBaseProvider.fireStore
    private val documentUsers = "users"

    // 🔹 One-time insert
    suspend fun insert(user: User): Flow<String?> = flow {
        try {
            db.collection(documentUsers)
                .document(user.id) // use userId as docId
                .set(user.toHashMap())
                .await()
            emit(user.id) // success
        } catch (e: Exception) {
            emit(null) // failure
        }
    }

    // 🔹 One-time update
    suspend fun updateUser(user: User): Flow<Boolean> = flow {
        try {
            db.collection(documentUsers)
                .document(user.id)
                .set(user.toHashMap())
                .await()
            emit(true)
        } catch (e: Exception) {
            emit(false)
        }
    }

    suspend fun getUser(userId : String) : Flow<User?> = flow {
        try {

            val snapshot = db.collection(documentUsers)
                .document(userId)
                .get()
                .await()
            val result = snapshot.toObject(User::class.java)
            emit(result)
        }catch (e : Exception) {
            emit(null)
        }
    }


    fun getUserRealtime(userId: String): Flow<User?> = callbackFlow {
        val listener = db.collection("users").document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(User::class.java))
            }
        awaitClose { listener.remove() }
    }



    private fun User.toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "name" to name,
            "email" to email,
            "organization" to organization
        )
    }

    private fun Map<String, Any>.toUser(): User {
        return User(
            id = this["id"] as String,
            name = this["name"] as String,
            email = this["email"] as String,
            organization = this["organization"] as String
        )
    }
}
