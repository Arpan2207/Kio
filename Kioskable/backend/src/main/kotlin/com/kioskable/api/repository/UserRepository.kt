package com.kioskable.api.repository

import com.kioskable.api.model.Role
import com.kioskable.api.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface UserRepository : MongoRepository<User, String> {
    fun findByEmail(email: String): User?
    
    fun findByBusinessId(businessId: String, pageable: Pageable): Page<User>
    
    fun findByBusinessIdAndRole(businessId: String, role: Role, pageable: Pageable): Page<User>
    
    fun findByBusinessIdAndIsEnabled(businessId: String, isEnabled: Boolean, pageable: Pageable): Page<User>
    
    fun countByBusinessId(businessId: String): Long
    
    fun findByLastLoginBefore(date: LocalDateTime): List<User>
    
    fun findByRole(role: Role): List<User>
} 