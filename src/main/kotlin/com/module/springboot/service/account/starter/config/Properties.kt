package com.module.springboot.service.account.starter.config


import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "service-account-security")
data class ServiceAccountSecurityProperties(
    /**
     * List of internal endpoints that require service account authentication.
     * These routes are only accessible with valid service account tokens.
     */
    val internalEndpoints: List<String>,

    /**
     * The name of the identity cookie key
     */
    val secret: String,
    val issuer: String = "service-account-starter",
)