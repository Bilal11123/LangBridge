package com.example.langbridge.contacts.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.langbridge.UserInfo
import com.example.langbridge.contacts.data.models.ContactResponse
import com.example.langbridge.contacts.data.repository.ContactRepository
import com.example.langbridge.contacts.data.repository.ContactRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactViewModel(): ViewModel() {

    var name = mutableStateOf(UserInfo.name)
    private val repository: ContactRepository = ContactRepositoryImpl()
    val contactResponse = mutableStateOf<ContactResponse?>(null)
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    init {
        getContacts(UserInfo.id)
    }

    private fun getContacts(id:String?) {
        viewModelScope.launch(Dispatchers.IO) {
//            isLoading.value = true
            try {
                val response = repository.getContacts(id)
                withContext(Dispatchers.Main) {
                    contactResponse.value = response
                }
            } catch (e: Exception) {
                // Handle exceptions
                withContext(Dispatchers.Main){
                    error.value = "Failed to fetch contacts: ${e.message}"
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                }
            }
        }
    }

}