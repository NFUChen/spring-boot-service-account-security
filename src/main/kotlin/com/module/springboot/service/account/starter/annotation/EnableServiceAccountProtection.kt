package com.module.springboot.service.account.starter.annotation

import com.module.springboot.service.account.starter.ServiceAccountSecurityConfiguration
import com.module.springboot.service.account.starter.config.MethodSecurityConfig
import org.springframework.context.annotation.Import


@Import(
    ServiceAccountSecurityConfiguration::class,
    MethodSecurityConfig::class
)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class EnableServiceAccountProtection