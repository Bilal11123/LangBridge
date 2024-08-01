package com.example.langbridge.contacts.data.repository

import com.example.langbridge.contacts.data.models.ContactResponse

interface ContactRepository {
    suspend fun getContacts(id: String?): ContactResponse
}