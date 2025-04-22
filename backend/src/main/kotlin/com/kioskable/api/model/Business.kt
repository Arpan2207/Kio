package com.kioskable.api.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "businesses")
data class Business(
    @Id
    val id: String? = null,
    
    @Indexed(unique = true)
    val name: String,
    
    val address: Address? = null,
    
    val contactEmail: String,
    
    val contactPhone: String? = null,
    
    val subscription: SubscriptionType = SubscriptionType.FREE,
    
    val subscriptionExpiryDate: LocalDateTime? = null,
    
    val maxDevices: Int = 1, // Default limit for free tier
    
    val logo: String? = null, // URL to logo image
    
    val settings: BusinessSettings = BusinessSettings(),
    
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class Address(
    val street: String,
    val city: String,
    val state: String,
    val zip: String,
    val country: String
)

enum class SubscriptionType {
    FREE, BASIC, PROFESSIONAL, ENTERPRISE
}

data class BusinessSettings(
    val theme: Theme = Theme.LIGHT,
    val primaryColor: String = "#3498db",
    val secondaryColor: String = "#2ecc71",
    val defaultTransitionEffect: TransitionEffect = TransitionEffect.FADE,
    val allowedModules: List<String> = listOf("display") // Module IDs
)

enum class Theme {
    LIGHT, DARK, CUSTOM
}

enum class TransitionEffect {
    NONE, FADE, SLIDE, ZOOM, FLIP
} 