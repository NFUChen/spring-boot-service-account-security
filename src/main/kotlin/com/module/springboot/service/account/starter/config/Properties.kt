package com.module.springboot.service.account.starter.config


import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "service-account-security")
data class ServiceAccountSecurityProperties(
    /**
     * List of routes that do not require authentication.
     * These routes are accessible without any security checks.
     */
    var internalEndpoints: List<String>,

    /**
     * The name of the identity cookie key
     */
    val secret: String,
    val issuer: String = "service-account-starter",
)