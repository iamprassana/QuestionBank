package com.example.question_bank.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object FireBaseProvider {

    val auth : FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val storage : FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    val fireStore : FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
}