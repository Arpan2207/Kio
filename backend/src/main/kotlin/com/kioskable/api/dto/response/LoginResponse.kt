package com.kioskable.api.dto.response

data class LoginResponse(
    val token: String,
    val user: UserDto
) 