package com.example.langbridge

import kotlinx.serialization.Serializable

sealed class Screens {
    @Serializable
    data class Messages(
        val contactId: String? = null,
        val receiverId: String? = null,
        val receiverName: String? = null
    ) : Screens()
}