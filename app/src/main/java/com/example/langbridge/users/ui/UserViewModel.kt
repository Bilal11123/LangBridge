package com.example.langbridge.users.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.langbridge.UserInfo
import com.example.langbridge.users.data.models.User
import com.example.langbridge.users.data.repository.UserRepository
import com.example.langbridge.users.data.repository.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel: ViewModel() {
    private val repository: UserRepository = UserRepositoryImpl()
    val users = mutableStateOf<List<User>?>(null)
    private val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    init {
        getUsers(UserInfo.id)
    }

    private fun getUsers(id:String?) {
        isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getUsers(id)
                withContext(Dispatchers.Main) {
                    users.value = response.users
                }
            } catch (e: Exception) {
                // Handle exceptions
                withContext(Dispatchers.Main){
                    error.value = "Failed to fetch users: ${e.message}"
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                }
            }
        }
    }
}