package com.module.springboot.service.account.starter

import com.module.springboot.service.account.starter.annotation.RequireScopes
import com.module.springboot.service.account.starter.view.ServiceAccount
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.stereotype.Service
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig
@SpringBootTest(
    classes = [ServiceAccountSecurityConfiguration::class, RequireScopeTest.TestConfig::class],
    properties = ["spring.profiles.active=test"]
)
class RequireScopeTest {

    @Autowired
    private lateinit var testService: TestService

    @Configuration
    class TestConfig {
        @Bean
        fun testService(): TestService = TestService()
    }

    @Service
    class TestService {
        @RequireScopes(scopes = ["read:data"])
        fun methodWithSingleScope(): String = "single scope success"

        @RequireScopes(scopes = ["read:data", "write:data"])
        fun methodWithMultipleScopes(): String = "multiple scopes success"

        @RequireScopes(scopes = ["admin:read", "admin:write", "admin:delete"])
        fun methodWithThreeScopes(): String = "three scopes success"

        @RequireScopes() // Empty scopes - should allow access
        fun methodWithEmptyScopes(): String = "empty scopes success"

        fun methodWithoutAnnotation(): String = "no annotation success"
    }

    @Test
    fun `should allow access when user has single required scope`() {
        // Arrange
        val serviceAccount = ServiceAccount("test-client", "test-user", setOf("read:data", "write:data"))
        val authorities = listOf(SimpleGrantedAuthority("SCOPE_read:data"), SimpleGrantedAuthority("SCOPE_write:data"))
        val authentication = UsernamePasswordAuthenticationToken(serviceAccount, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        // Act & Assert - should not throw exception
        val result = testService.methodWithSingleScope()
        assert(result == "single scope success")
    }

    @Test
    fun `should allow access when user has all multiple required scopes`() {
        // Arrange
        val serviceAccount = ServiceAccount("test-client", "test-user", setOf("read:data", "write:data", "admin:read"))
        val authorities = listOf(
            SimpleGrantedAuthority("SCOPE_read:data"), 
            SimpleGrantedAuthority("SCOPE_write:data"),
            SimpleGrantedAuthority("SCOPE_admin:read")
        )
        val authentication = UsernamePasswordAuthenticationToken(serviceAccount, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        // Act & Assert - should not throw exception
        val result = testService.methodWithMultipleScopes()
        assert(result == "multiple scopes success")
    }

    @Test
    fun `should deny access when user lacks some required scopes`() {
        // Arrange
        val serviceAccount = ServiceAccount("test-client", "test-user", setOf("read:data")) // Missing write:data
        val authorities = listOf(SimpleGrantedAuthority("SCOPE_read:data"))
        val authentication = UsernamePasswordAuthenticationToken(serviceAccount, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        // Act & Assert
        assertThrows<AccessDeniedException> {
            testService.methodWithMultipleScopes()
        }
    }

    @Test
    fun `should deny access when user has no required scopes`() {
        // Arrange
        val serviceAccount = ServiceAccount("test-client", "test-user", setOf("other:scope"))
        val authorities = listOf(SimpleGrantedAuthority("SCOPE_other:scope"))
        val authentication = UsernamePasswordAuthenticationToken(serviceAccount, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        // Act & Assert
        assertThrows<AccessDeniedException> {
            testService.methodWithSingleScope()
        }
    }

    @Test
    fun `should deny access when user has partial scopes for three-scope method`() {
        // Arrange
        val serviceAccount = ServiceAccount("test-client", "test-user", setOf("admin:read", "admin:write")) // Missing admin:delete
        val authorities = listOf(
            SimpleGrantedAuthority("SCOPE_admin:read"),
            SimpleGrantedAuthority("SCOPE_admin:write")
        )
        val authentication = UsernamePasswordAuthenticationToken(serviceAccount, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        // Act & Assert
        assertThrows<AccessDeniedException> {
            testService.methodWithThreeScopes()
        }
    }

    @Test
    fun `should allow access when user has all three required scopes`() {
        // Arrange
        val serviceAccount = ServiceAccount("test-client", "test-user", setOf("admin:read", "admin:write", "admin:delete"))
        val authorities = listOf(
            SimpleGrantedAuthority("SCOPE_admin:read"),
            SimpleGrantedAuthority("SCOPE_admin:write"),
            SimpleGrantedAuthority("SCOPE_admin:delete")
        )
        val authentication = UsernamePasswordAuthenticationToken(serviceAccount, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        // Act & Assert
        val result = testService.methodWithThreeScopes()
        assert(result == "three scopes success")
    }

    @Test
    fun `should deny access when no authentication present`() {
        // Arrange
        SecurityContextHolder.clearContext()

        // Act & Assert
        assertThrows<AccessDeniedException> {
            testService.methodWithSingleScope()
        }
    }

    @Test
    fun `should allow access to methods with empty scopes annotation`() {
        // Arrange
        val serviceAccount = ServiceAccount("test-client", "test-user", setOf("some:scope"))
        val authorities = listOf(SimpleGrantedAuthority("SCOPE_some:scope"))
        val authentication = UsernamePasswordAuthenticationToken(serviceAccount, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        // Act & Assert
        val result = testService.methodWithEmptyScopes()
        assert(result == "empty scopes success")
    }

    @Test
    fun `should allow access to methods without scope annotation`() {
        // Arrange
        val serviceAccount = ServiceAccount("test-client", "test-user", setOf("some:scope"))
        val authorities = listOf(SimpleGrantedAuthority("SCOPE_some:scope"))
        val authentication = UsernamePasswordAuthenticationToken(serviceAccount, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        // Act & Assert
        val result = testService.methodWithoutAnnotation()
        assert(result == "no annotation success")
    }
}
