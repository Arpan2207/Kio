package com.kioskable.app.data.remote.dto

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val businessId: String? = null,
    val phoneNumber: String? = null
) 