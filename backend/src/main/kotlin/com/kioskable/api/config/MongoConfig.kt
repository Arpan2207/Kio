package com.kioskable.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@Configuration
@EnableMongoRepositories(basePackages = ["com.kioskable.api.repository"])
@EnableMongoAuditing
class MongoConfig {
    
    @Bean
    fun validatingMongoEventListener(factory: LocalValidatorFactoryBean): ValidatingMongoEventListener {
        return ValidatingMongoEventListener(factory)
    }
    
    @Bean
    fun validator(): LocalValidatorFactoryBean {
        return LocalValidatorFactoryBean()
    }
} 