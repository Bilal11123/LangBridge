package com.example.langbridge.contacts.data.repository

import com.example.langbridge.contacts.data.models.ContactResponse
import com.example.langbridge.contacts.data.models.LanguageChangeResponse

interface ContactRepository {
    suspend fun getContacts(id: String?): ContactResponse
    suspend fun changeLanguageServerside(userId: String?, language: String?): LanguageChangeResponse
}