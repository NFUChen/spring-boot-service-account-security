package com.module.springboot.service.account.starter

import com.module.springboot.service.account.starter.config.ServiceAccountSecurityProperties
import com.module.springboot.service.account.starter.service.ServiceAccountJwtService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ServiceAccountSecurityProperties::class)
@ComponentScan(basePackages = ["com.module.springboot.service.account.starter"])
class ServiceAccountSecurityConfiguration {

    @Bean("serviceAccountDefaultJwtService")
    fun jwtService(properties: ServiceAccountSecurityProperties): ServiceAccountJwtService {
        return ServiceAccountJwtService(properties)
    }
}