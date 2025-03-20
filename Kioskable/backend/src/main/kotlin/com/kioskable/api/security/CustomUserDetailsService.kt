package com.kioskable.api.security

import com.kioskable.api.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("User not found with email: $username")
        
        // If user is disabled, don't allow login
        if (!user.isEnabled) {
            throw UsernameNotFoundException("User account is disabled")
        }
        
        // Create authorities from user role and permissions
        val authorities = mutableListOf<SimpleGrantedAuthority>()
        
        // Add role-based authority
        authorities.add(SimpleGrantedAuthority("ROLE_${user.role.name}"))
        
        // Add permission-based authorities
        user.permissions.forEach { permission ->
            authorities.add(SimpleGrantedAuthority(permission))
        }
        
        return User(
            user.email,
            user.password,
            authorities
        )
    }
} 