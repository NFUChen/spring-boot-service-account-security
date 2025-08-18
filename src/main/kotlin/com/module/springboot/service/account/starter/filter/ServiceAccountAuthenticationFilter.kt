package com.module.springboot.service.account.starter.filter

import com.module.springboot.service.account.starter.config.ServiceAccountSecurityProperties
import com.module.springboot.service.account.starter.service.ServiceAccountJwtService
import com.module.springboot.service.account.starter.view.ServiceAccount
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ServiceAccountAuthenticationFilter(
    private val serviceAccountJwtService: ServiceAccountJwtService,
    private val serviceAccountSecurityProperties: ServiceAccountSecurityProperties
): OncePerRequestFilter() {
    
    private val pathMatcher = AntPathMatcher()
    private val BEARER_PREFIX = "Bearer "
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestPath = request.requestURI
        
        // Check if the request path is in the unprotected routes
        if (isUnprotectedRoute(requestPath)) {
            filterChain.doFilter(request, response)
            return
        }
        
        // Extract JWT token from request
        val token = extractToken(request)

        if (token == null) {
            SecurityContextHolder.clearContext()
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("{\"error\":\"Authentication required\"}")
            response.contentType = "application/json"
            return
        }

        if (!serviceAccountJwtService.isValidToken(token)) {
            // Token exists but is invalid, clear any existing authentication
            SecurityContextHolder.clearContext()
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("{\"error\":\"Invalid or expired token\"}")
            response.contentType = "application/json"
            return
        }
        

        // Parse the service account from the token
        val serviceAccount = serviceAccountJwtService.parseToken(token) as ServiceAccount
        // Create authorities from scopes
        val authorities = serviceAccount.scopes.map { scope ->
            SimpleGrantedAuthority("SCOPE_$scope")
        }

        // Create authentication token
        val authToken = UsernamePasswordAuthenticationToken(
            serviceAccount,
            null,
            authorities
        )
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

        // Set the authentication in the security context
        SecurityContextHolder.getContext().authentication = authToken
        
        filterChain.doFilter(request, response)
    }
    
    private fun isUnprotectedRoute(requestPath: String): Boolean {
        return serviceAccountSecurityProperties.unprotectedRoutes.any { pattern ->
            pathMatcher.match(pattern, requestPath)
        }
    }
    
    private fun extractToken(request: HttpServletRequest): String? {
        // First try to get token from Authorization header (Bearer token)
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length)
        }
        
        // Then try to get token from cookie
        val cookies = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                if (cookie.name == serviceAccountSecurityProperties.identityKey) {
                    return cookie.value
                }
            }
        }
        
        return null
    }
}