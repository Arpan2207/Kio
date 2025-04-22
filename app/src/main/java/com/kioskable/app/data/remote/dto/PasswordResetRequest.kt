package com.kioskable.app.data.remote.dto

data class PasswordResetRequest(
    val email: String,
    val newPassword: String
) 