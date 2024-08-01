package com.example.langbridge.contacts.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Contact(
    @SerialName("name") val name: String?,
    @SerialName("contact_id") val contactId: String?,
    @SerialName("last_message") val lastMessage: String?
)

@Serializable
data class ContactResponse(
    @SerialName("contacts") val contacts: List<Contact>?
)
