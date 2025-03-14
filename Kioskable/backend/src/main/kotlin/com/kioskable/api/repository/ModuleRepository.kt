package com.kioskable.api.repository

import com.kioskable.api.model.Module
import com.kioskable.api.model.SubscriptionType
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ModuleRepository : MongoRepository<Module, String> {
    fun findByCode(code: String): Module?
    
    fun findByIsActiveTrue(): List<Module>
    
    fun findByIsCore(isCore: Boolean): List<Module>
    
    fun findByMinimumSubscriptionLessThanEqual(subscriptionType: SubscriptionType): List<Module>
} 