package com.kioskable.app.data.remote.dto

data class LoginResponse(
    val token: String,
    val user: UserDto
) 