package com.kioskable.api.dto.request

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "First name is required")
    val firstName: String,
    
    @field:NotBlank(message = "Last name is required")
    val lastName: String,
    
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,
    
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String,
    
    val businessId: String? = null,
    
    val phoneNumber: String? = null
) 