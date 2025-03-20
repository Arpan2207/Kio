package com.kioskable.api.dto.response

import com.kioskable.api.dto.response.UserDto

data class RegisterResponse(
    val token: String,
    val user: UserDto
) 