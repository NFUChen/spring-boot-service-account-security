package com.module.springboot.service.account.starter.config


import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    /**
     * JWT secret key for signing and verifying tokens
     */
    val secret: String
)

@ConfigurationProperties(prefix = "security")
data class SecurityProperties(
    /**
     * List of routes that do not require authentication.
     * These routes are accessible without any security checks.
     */
    val unprotectedRoutes: List<String> = listOf("/public/**", "/health", "/actuator/**"),

    /**
     * The name of the identity cookie key
     */
    val identityKey: String = "jwt"
)