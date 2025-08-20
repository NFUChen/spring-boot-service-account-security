package com.module.springboot.service.account.starter

import com.module.springboot.service.account.starter.config.ServiceAccountSecurityProperties
import com.module.springboot.service.account.starter.filter.ServiceAccountAuthenticationFilter
import com.module.springboot.service.account.starter.service.ServiceAccountJwtService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
class JwtConfiguration {
    @Bean("serviceAccountDefaultJwtService")
    fun jwtService(properties: ServiceAccountSecurityProperties): ServiceAccountJwtService {
        return ServiceAccountJwtService(properties)
    }
}

@Configuration
@EnableConfigurationProperties(ServiceAccountSecurityProperties::class)
@ComponentScan(basePackages = ["com.module.springboot.service.account.starter"])
class ServiceAccountSecurityConfiguration(
    val serviceAccountJwtService: ServiceAccountJwtService,
    val serviceAccountSecurityProperties: ServiceAccountSecurityProperties
) {
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @Order(1)
    fun serviceAccountSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher(*serviceAccountSecurityProperties.internalEndpoints.toTypedArray())
            .addFilterBefore(serviceAccountAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests { it.anyRequest().authenticated() }
        return http.build()
    }

    @Bean
    fun serviceAccountAuthenticationFilter(
    ): ServiceAccountAuthenticationFilter {
        return ServiceAccountAuthenticationFilter(serviceAccountJwtService, serviceAccountSecurityProperties)
    }
}