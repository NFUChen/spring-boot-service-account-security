package com.module.springboot.service.account.starter

import com.module.springboot.service.account.starter.aspect.RequireScopeAspect
import com.module.springboot.service.account.starter.config.MethodSecurityConfig
import com.module.springboot.service.account.starter.config.ServiceAccountSecurityProperties
import com.module.springboot.service.account.starter.service.ServiceAccountJwtService
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource

@TestConfiguration
@EnableAspectJAutoProxy
@Import(MethodSecurityConfig::class)
@TestPropertySource(properties = [
    "service-account-security.secret=dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLWluLXVuaXQtdGVzdHMtb25seQ==",
    "service-account-security.internal-endpoints[0]=/public/**",
    "service-account-security.internal-endpoints[1]=/health",
    "service-account-security.internal-endpoints[2]=/actuator/**",
    "service-account-security.issuer=test-issuer"
])
class TestConfiguration {

    @Bean
    fun serviceAccountSecurityProperties(): ServiceAccountSecurityProperties {
        return ServiceAccountSecurityProperties(
            internalEndpoints = listOf("/public/**", "/health", "/actuator/**"),
            secret = "dGVzdC1zZWNyZXQtZm9yLWp3dC10b2tlbi12YWxpZGF0aW9uLWluLXVuaXQtdGVzdHMtb25seQ==",
            issuer = "test-issuer"
        )
    }

    @Bean
    fun serviceAccountJwtService(properties: ServiceAccountSecurityProperties): ServiceAccountJwtService {
        return ServiceAccountJwtService(properties)
    }

    @Bean
    fun requireScopeAspect(): RequireScopeAspect {
        return RequireScopeAspect()
    }
}
