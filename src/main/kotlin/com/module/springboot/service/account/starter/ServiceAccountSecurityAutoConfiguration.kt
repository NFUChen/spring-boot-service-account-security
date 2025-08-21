package com.module.springboot.service.account.starter

import com.module.springboot.service.account.starter.config.ServiceAccountSecurityProperties
import com.module.springboot.service.account.starter.filter.ServiceAccountAuthenticationFilter
import com.module.springboot.service.account.starter.service.ServiceAccountJwtService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping


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
    val serviceAccountSecurityProperties: ServiceAccountSecurityProperties,
    @Autowired(required = false)
    @Qualifier("requestMappingHandlerMapping")
    val requestMapping: RequestMappingHandlerMapping
) {
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @Order(1)
    fun serviceAccountSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.securityMatcher(*serviceAccountSecurityProperties.internalEndpoints.toTypedArray())
            .sessionManagement { sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(serviceAccountAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests { it.anyRequest().authenticated() }
            .csrf { it.disable() }
        return http.build()
    }

    @Bean
    fun serviceAccountAuthenticationFilter(
    ): ServiceAccountAuthenticationFilter {
        return ServiceAccountAuthenticationFilter(serviceAccountJwtService, serviceAccountSecurityProperties, requestMapping)
    }
}