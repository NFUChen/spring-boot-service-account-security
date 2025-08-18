package com.module.springboot.service.account.starter

import com.module.springboot.service.account.starter.annotation.RequireScope
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
        @RequireScope(scope = "read:data")
        fun methodWithScope(): String = "success"

        fun methodWithoutScope(): String = "success"
    }

    @Test
    fun `should allow access when user has required scope`() {
        // Arrange
        val serviceAccount = ServiceAccount("test-client", "test-user", setOf("read:data", "write:data"))
        val authorities = listOf(SimpleGrantedAuthority("SCOPE_read:data"), SimpleGrantedAuthority("SCOPE_write:data"))
        val authentication = UsernamePasswordAuthenticationToken(serviceAccount, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        // Act & Assert - should not throw exception
        val result = testService.methodWithScope()
        assert(result == "success")
    }

    @Test
    fun `should deny access when user lacks required scope`() {
        // Arrange
        val serviceAccount = ServiceAccount("test-client", "test-user", setOf("write:data"))
        val authorities = listOf(SimpleGrantedAuthority("SCOPE_write:data"))
        val authentication = UsernamePasswordAuthenticationToken(serviceAccount, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        // Act & Assert
        assertThrows<AccessDeniedException> {
            testService.methodWithScope()
        }
    }

    @Test
    fun `should deny access when no authentication present`() {
        // Arrange
        SecurityContextHolder.clearContext()

        // Act & Assert
        assertThrows<AccessDeniedException> {
            testService.methodWithScope()
        }
    }

    @Test
    fun `should allow access to methods without scope annotation`() {
        // Arrange
        val serviceAccount = ServiceAccount("test-client", "test-user", setOf("some:scope"))
        val authorities = listOf(SimpleGrantedAuthority("SCOPE_some:scope"))
        val authentication = UsernamePasswordAuthenticationToken(serviceAccount, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        // Act & Assert
        val result = testService.methodWithoutScope()
        assert(result == "success")
    }
}
