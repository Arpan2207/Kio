package com.kioskable.api.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    
    @Bean
    fun api(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Kioskable API")
                    .description("API for Kioskable, an Android kiosk application that transforms standard devices into secure, feature-rich display terminals.")
                    .version("v1.0.0")
                    .contact(
                        Contact()
                            .name("Kioskable Team")
                            .email("support@kioskable.com")
                    )
                    .license(
                        License()
                            .name("Private")
                    )
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "bearer-jwt", 
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .`in`(SecurityScheme.In.HEADER)
                            .name("Authorization")
                    )
            )
            .addSecurityItem(
                SecurityRequirement()
                    .addList("bearer-jwt")
            )
    }
} 