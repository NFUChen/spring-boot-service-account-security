package com.module.springboot.service.account.starter.aspect

import com.module.springboot.service.account.starter.annotation.RequireScope
import com.module.springboot.service.account.starter.view.ServiceAccount
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Aspect
@Component
class RequireScopeAspect {

    @Before("@annotation(requireScope)")
    fun checkScope(joinPoint: JoinPoint, requireScope: RequireScope) {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw AccessDeniedException("No authentication found")

        val serviceAccount = authentication.principal as? ServiceAccount
            ?: throw AccessDeniedException("Invalid authentication principal")

        val requiredScope = requireScope.scope
        if (!serviceAccount.scopes.contains(requiredScope)) {
            throw AccessDeniedException("Missing required scope: $requiredScope")
        }
    }
}
