package com.kioskable.api.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.HashMap

@RestController
@RequestMapping("/health")
class HealthController {
    
    @GetMapping
    fun healthCheck(): ResponseEntity<Map<String, Any>> {
        val response = HashMap<String, Any>()
        response["status"] = "UP"
        response["timestamp"] = LocalDateTime.now().toString()
        response["service"] = "Kioskable API"
        
        return ResponseEntity.ok(response)
    }
} 