package com.kioskable.api.repository

import com.kioskable.api.model.Business
import com.kioskable.api.model.SubscriptionType
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface BusinessRepository : MongoRepository<Business, String> {
    fun findByName(name: String): Business?
    fun findBySubscriptionExpiryDateBefore(date: LocalDateTime): List<Business>
    fun findBySubscription(subscriptionType: SubscriptionType): List<Business>
} 