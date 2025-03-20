package com.kioskable.app.data.remote.dto

data class RegisterResponse(
    val token: String,
    val user: UserDto
) 