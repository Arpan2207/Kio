package com.kioskable.api.service

import com.kioskable.api.dto.request.LoginRequest
import com.kioskable.api.dto.response.LoginResponse
import com.kioskable.api.dto.response.UserDto
import com.kioskable.api.repository.UserRepository
import com.kioskable.api.security.JwtTokenProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository
) {
    
    fun login(loginRequest: LoginRequest): LoginResponse {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
        )
        
        SecurityContextHolder.getContext().authentication = authentication
        
        val userDetails = authentication.principal as UserDetails
        val token = jwtTokenProvider.generateToken(userDetails)
        
        val user = userRepository.findByEmail(loginRequest.email).orElseThrow()
        
        // Update last login time
        user.lastLogin = LocalDateTime.now()
        userRepository.save(user)
        
        return LoginResponse(
            token = token,
            user = UserDto.fromEntity(user)
        )
    }
} 