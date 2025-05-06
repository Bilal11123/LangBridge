package com.example.langbridge.admin_dashboard.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.langbridge.admin_dashboard.data.models.AdminUser
import com.example.langbridge.admin_dashboard.data.repository.AdminRepository
import com.example.langbridge.admin_dashboard.data.repository.AdminRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminDashboardViewModel : ViewModel() {
    private val repository: AdminRepository = AdminRepositoryImpl()
    val userList = mutableStateOf<List<AdminUser>>(emptyList())
    val isLoading = mutableStateOf(false)

    init {
        fetchUserInteractions()
    }

    fun fetchUserInteractions() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getUserInteractions()
            withContext(Dispatchers.Main) {
                userList.value = response
                isLoading.value = false
            }
        }
    }
}
