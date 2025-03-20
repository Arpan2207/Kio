package com.kioskable.api.config

import com.kioskable.api.service.FileStorageService
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StorageConfig {
    
    @Bean
    fun initStorage(fileStorageService: FileStorageService): CommandLineRunner {
        return CommandLineRunner {
            fileStorageService.init()
        }
    }
} 