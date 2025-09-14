package com.example.question_bank.repositories.viewModels.loginViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.question_bank.repositories.FireBaseProvider
import com.example.question_bank.repositories.viewModels.loginViewModel.dataViewModel.Organization
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SearchViewModel : ViewModel() {

    val fireStore = FireBaseProvider.fireStore

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery : StateFlow<String> = _searchQuery

    private val _responseOrganization = MutableStateFlow<List<Organization>>(emptyList())
    val responseOrganization : StateFlow<List<Organization>> = _responseOrganization

    fun updateQuery(query : String) {
        _searchQuery.value = query
        performSearch(query)
    }

    fun performSearch(query : String) {

        if(query.isBlank()) {
            _responseOrganization.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                val snapShot = fireStore.collection("organizations")
                    .whereGreaterThanOrEqualTo("Name", query)
                    .whereLessThanOrEqualTo("Name", query + "\uf8ff")
                    .get()
                    .await()

                val results = snapShot.documents.mapNotNull { it ->
                    it.toObject(Organization::class.java)?.copy(id = it.id)
                }

                _responseOrganization.value = results

            }catch (e : Exception) {
                e.printStackTrace()
                _responseOrganization.value = emptyList()
            }
        }
    }

    fun clear() {
        _searchQuery.value = ""
        _responseOrganization.value = emptyList()
    }
}