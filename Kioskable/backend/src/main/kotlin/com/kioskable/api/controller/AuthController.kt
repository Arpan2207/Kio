package com.kioskable.api.controller

import com.kioskable.api.dto.request.LoginRequest
import com.kioskable.api.dto.request.PasswordResetRequest
import com.kioskable.api.dto.request.RegisterRequest
import com.kioskable.api.dto.response.LoginResponse
import com.kioskable.api.dto.response.MessageResponse
import com.kioskable.api.dto.response.RegisterResponse
import com.kioskable.api.dto.response.UserDto
import com.kioskable.api.service.AuthService
import com.kioskable.api.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val userService: UserService
) {
    
    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val loginResponse = authService.login(loginRequest)
        return ResponseEntity.ok(loginResponse)
    }
    
    @PostMapping("/register")
    fun register(@Valid @RequestBody registerRequest: RegisterRequest): ResponseEntity<RegisterResponse> {
        val registerResponse = authService.register(registerRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse)
    }
    
    @PostMapping("/reset-password")
    fun resetPassword(@Valid @RequestBody passwordResetRequest: PasswordResetRequest): ResponseEntity<MessageResponse> {
        val response = authService.resetPassword(passwordResetRequest)
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<UserDto> {
        val userDto = userService.getCurrentUser(userDetails.username)
        return ResponseEntity.ok(userDto)
    }
} 