package com.example.langbridge.contacts.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.mutableStateOf
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.langbridge.UserInfo
import com.example.langbridge.contacts.data.models.ContactResponse
import com.example.langbridge.contacts.data.repository.ContactRepository
import com.example.langbridge.contacts.data.repository.ContactRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactViewModel : ViewModel() {

    var name = mutableStateOf(UserInfo.name)
    private val repository: ContactRepository = ContactRepositoryImpl()
    val contactResponse = mutableStateOf<ContactResponse?>(null)
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    init {
        getContacts()
    }

    fun getContacts() {
        isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getContacts(UserInfo.id)
                withContext(Dispatchers.Main) {
                    contactResponse.value = response
                }
            } catch (e: Exception) {
                // Handle exceptions
                withContext(Dispatchers.Main) {
                    error.value = "Failed to fetch contacts: ${e.message}"
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                }
            }
        }
    }

    fun changeLanguage(language: String?) {

        viewModelScope.launch {
            try {
                val response = repository.changeLanguageServerside(UserInfo.id, language)
                withContext(Dispatchers.Main) {
                    if (response.status == "Success") {
                        UserInfo.language = language
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))

                    }
                }
            } catch (e: Exception) {
                // Handle exceptions
                withContext(Dispatchers.Main) {
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