package com.module.springboot.service.account.starter

import com.module.springboot.service.account.starter.view.ServiceAccount
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.stereotype.Service
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import

/**
 * 展示如何使用 @PreAuthorize 搭配 SCOPE_ authorities 進行權限檢查
 */
@SpringJUnitConfig
@SpringBootTest(
    classes = [TestConfiguration::class, PreAuthorizeExampleTest.TestConfig::class],
    properties = ["spring.profiles.active=test"]
)
@Import(TestConfiguration::class)
class PreAuthorizeExampleTest {

    @Autowired
    private lateinit var exampleService: ExampleService

    @Configuration
    class TestConfig {
        @Bean
        fun exampleService(): ExampleService = ExampleService()
    }

    @Service
    class ExampleService {
        
        @PreAuthorize("hasAuthority('SCOPE_read:data')")
        fun readData(): String = "data read successfully"

        @PreAuthorize("hasAuthority('SCOPE_admin:read') and hasAuthority('SCOPE_admin:write')")
        fun adminOperation(): String = "admin operation completed"

        @PreAuthorize("hasAnyAuthority('SCOPE_read:data', 'SCOPE_read:public')")
        fun readAnyData(): String = "any data read successfully"
    }

    @Test
    fun `should allow access with correct single scope`() {
        // Arrange
        val serviceAccount = ServiceAccount("test-client", "test-user", setOf("read:data"))
        val authorities = listOf(SimpleGrantedAuthority("SCOPE_read:data"))
        val authentication = UsernamePasswordAuthenticationToken(serviceAccount, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        // Act & Assert
        val result = exampleService.readData()
        assert(result == "data read successfully")
    }

    @Test
    fun `should deny access without required scope`() {
        // Arrange
        val serviceAccount = ServiceAccount("test-client", "test-user", setOf("write:data"))
        val authorities = listOf(SimpleGrantedAuthority("SCOPE_write:data"))
        val authentication = UsernamePasswordAuthenticationToken(serviceAccount, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        // Act & Assert
        assertThrows<AccessDeniedException> {
            exampleService.readData()
        }
    }

    @Test
    fun `should allow access with all required scopes`() {
        // Arrange
        val serviceAccount = ServiceAccount("test-client", "test-user", setOf("admin:read", "admin:write"))
        val authorities = listOf(
            SimpleGrantedAuthority("SCOPE_admin:read"),
            SimpleGrantedAuthority("SCOPE_admin:write")
        )
        val authentication = UsernamePasswordAuthenticationToken(serviceAccount, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        // Act & Assert
        val result = exampleService.adminOperation()
        assert(result == "admin operation completed")
    }

    @Test
    fun `should deny access with partial required scopes`() {
        // Arrange
        val serviceAccount = ServiceAccount("test-client", "test-user", setOf("admin:read"))
        val authorities = listOf(SimpleGrantedAuthority("SCOPE_admin:read"))
        val authentication = UsernamePasswordAuthenticationToken(serviceAccount, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        // Act & Assert
        assertThrows<AccessDeniedException> {
            exampleService.adminOperation()
        }
    }

    @Test
    fun `should allow access with any required scope`() {
        // Arrange
        val serviceAccount = ServiceAccount("test-client", "test-user", setOf("read:public"))
        val authorities = listOf(SimpleGrantedAuthority("SCOPE_read:public"))
        val authentication = UsernamePasswordAuthenticationToken(serviceAccount, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        // Act & Assert
        val result = exampleService.readAnyData()
        assert(result == "any data read successfully")
    }
}
