package com.example.langbridge.settings.ui


import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.langbridge.UserInfo
import com.example.langbridge.contacts.data.models.LanguageChangeResponse
import com.example.langbridge.settings.data.repository.GenericResponse
import com.example.langbridge.settings.data.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel : ViewModel() {

    private val repository = SettingsRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _languageChangeResponse = MutableStateFlow<LanguageChangeResponse?>(null)
    val languageChangeResponse = _languageChangeResponse.asStateFlow()

    private val _nameChangeResponse = MutableStateFlow<GenericResponse?>(null)
    val nameChangeResponse = _nameChangeResponse.asStateFlow()

    private val _passwordChangeResponse = MutableStateFlow<GenericResponse?>(null)
    val passwordChangeResponse = _passwordChangeResponse.asStateFlow()

    fun changeLanguage(languageCode: String?) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.changeLanguageServerside(UserInfo.id, languageCode)
                withContext(Dispatchers.Main) {
                    if (response.status == "Success") {
                        UserInfo.language = languageCode
                        // ✅ Switch local app locale to the selected language
                        AppCompatDelegate.setApplicationLocales(
                            androidx.core.os.LocaleListCompat.forLanguageTags(languageCode)
                        )
                    }
                    _languageChangeResponse.value = response
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _languageChangeResponse.value = LanguageChangeResponse(status = "Failed")
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
    }


    fun changeName(newName: String) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.changeNameServerside(UserInfo.id, newName)
                withContext(Dispatchers.Main) {
                    _nameChangeResponse.value = response
                    if (response.status == "success") {
                        UserInfo.name.value = newName
                    }

                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _nameChangeResponse.value = GenericResponse(status = "Failed", message = "Failed to change name")
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
    }

    fun changePassword(newPassword: String) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.changePasswordServerside(UserInfo.id, newPassword)
                withContext(Dispatchers.Main) {
                    _passwordChangeResponse.value = response
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _passwordChangeResponse.value = GenericResponse(status = "Failed", message = "Failed to change password")
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
    }

    fun resetResponses() {
        _languageChangeResponse.value = null
        _nameChangeResponse.value = null
        _passwordChangeResponse.value = null
    }
}
