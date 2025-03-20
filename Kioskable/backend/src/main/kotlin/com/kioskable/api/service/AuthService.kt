package com.kioskable.api.service

import com.kioskable.api.dto.request.LoginRequest
import com.kioskable.api.dto.request.PasswordResetRequest
import com.kioskable.api.dto.request.RegisterRequest
import com.kioskable.api.dto.response.LoginResponse
import com.kioskable.api.dto.response.MessageResponse
import com.kioskable.api.dto.response.RegisterResponse
import com.kioskable.api.dto.response.UserDto
import com.kioskable.api.exception.EmailAlreadyExistsException
import com.kioskable.api.exception.ResourceNotFoundException
import com.kioskable.api.model.Role
import com.kioskable.api.model.User
import com.kioskable.api.repository.UserRepository
import com.kioskable.api.security.JwtTokenProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    
    fun login(loginRequest: LoginRequest): LoginResponse {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
        )
        
        SecurityContextHolder.getContext().authentication = authentication
        
        val userDetails = authentication.principal as UserDetails
        val token = jwtTokenProvider.generateToken(userDetails)
        
        val user = userRepository.findByEmail(loginRequest.email)
            ?: throw RuntimeException("User not found")
        
        // Update last login time
        user.lastLogin = LocalDateTime.now()
        userRepository.save(user)
        
        return LoginResponse(
            token = token,
            user = UserDto.fromEntity(user)
        )
    }
    
    fun register(registerRequest: RegisterRequest): RegisterResponse {
        // Check if user already exists
        if (userRepository.findByEmail(registerRequest.email) != null) {
            throw EmailAlreadyExistsException("Email ${registerRequest.email} is already registered")
        }
        
        // Create new user
        val newUser = User(
            firstName = registerRequest.firstName,
            lastName = registerRequest.lastName,
            email = registerRequest.email,
            password = passwordEncoder.encode(registerRequest.password),
            businessId = registerRequest.businessId,
            phoneNumber = registerRequest.phoneNumber,
            role = if (registerRequest.businessId == null) Role.SYSTEM_ADMIN else Role.BUSINESS_ADMIN,
            isEnabled = true,
            permissions = getDefaultPermissions(registerRequest.businessId == null)
        )
        
        val savedUser = userRepository.save(newUser)
        
        // Generate token for the new user
        val token = jwtTokenProvider.generateToken(
            org.springframework.security.core.userdetails.User(
                savedUser.email,
                savedUser.password,
                getAuthorities(savedUser.role)
            )
        )
        
        return RegisterResponse(
            token = token,
            user = UserDto.fromEntity(savedUser)
        )
    }
    
    fun resetPassword(passwordResetRequest: PasswordResetRequest): MessageResponse {
        val user = userRepository.findByEmail(passwordResetRequest.email)
            ?: throw ResourceNotFoundException("User with email ${passwordResetRequest.email} not found")
        
        user.password = passwordEncoder.encode(passwordResetRequest.newPassword)
        userRepository.save(user)
        
        return MessageResponse("Password has been reset successfully")
    }
    
    private fun getDefaultPermissions(isSystemAdmin: Boolean): List<String> {
        return if (isSystemAdmin) {
            listOf("ADMIN_ALL")
        } else {
            listOf("MANAGE_CONTENT", "MANAGE_DEVICES", "VIEW_ANALYTICS")
        }
    }
    
    private fun getAuthorities(role: Role): List<org.springframework.security.core.authority.SimpleGrantedAuthority> {
        return when (role) {
            Role.SYSTEM_ADMIN -> listOf(org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_SYSTEM_ADMIN"))
            Role.BUSINESS_ADMIN -> listOf(org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_BUSINESS_ADMIN"))
            Role.CONTENT_MANAGER -> listOf(org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_CONTENT_MANAGER"))
            Role.DEVICE_MANAGER -> listOf(org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_DEVICE_MANAGER"))
            Role.VIEWER -> listOf(org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_VIEWER"))
        }
    }
} 