package com.module.springboot.service.account.starter

import com.module.springboot.service.account.starter.config.JwtProperties
import com.module.springboot.service.account.starter.config.SecurityProperties
import com.module.springboot.service.account.starter.service.DefaultJwtService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(JwtProperties::class, SecurityProperties::class)
@ComponentScan(basePackages = ["com.module.springboot.service.account.starter"])
class ServiceAccountSecurityConfiguration {

    @Bean
    fun defaultJwtService(jwtProperties: JwtProperties): DefaultJwtService {
        return DefaultJwtService("service-account-starter", jwtProperties)
    }
}