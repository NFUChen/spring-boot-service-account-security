package com.module.springboot.service.account.starter.aspect

import com.module.springboot.service.account.starter.annotation.RequireScopes
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

    @Before("@annotation(requireScopes)")
    fun checkScopes(joinPoint: JoinPoint, requireScopes: RequireScopes) {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw AccessDeniedException("No authentication found")

        val serviceAccount = authentication.principal as? ServiceAccount
            ?: throw AccessDeniedException("Invalid authentication principal")

        val requiredScopes = requireScopes.scopes
        
        // If no scopes specified, allow access (backward compatibility)
        if (requiredScopes.isEmpty()) {
            return
        }
        
        // Check if user has all required scopes
        val missingScopes = requiredScopes.filter { requiredScope ->
            !serviceAccount.scopes.contains(requiredScope)
        }
        
        if (missingScopes.isNotEmpty()) {
            throw AccessDeniedException("Missing required scopes: ${missingScopes.joinToString(", ")}")
        }
    }
}
