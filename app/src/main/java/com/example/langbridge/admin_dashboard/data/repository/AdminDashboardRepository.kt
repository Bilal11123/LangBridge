package com.example.langbridge.admin_dashboard.data.repository

import com.example.langbridge.admin_dashboard.data.models.AdminUser

interface AdminRepository {
    suspend fun getUserInteractions(): List<AdminUser>
}
