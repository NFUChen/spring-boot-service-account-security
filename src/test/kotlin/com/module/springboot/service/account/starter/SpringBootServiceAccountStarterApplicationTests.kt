package com.module.springboot.service.account.starter

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [ServiceAccountSecurityConfiguration::class],
    properties = ["spring.profiles.active=test"]
)
class SpringBootServiceAccountStarterApplicationTests {

    @Test
    fun contextLoads() {
    }

}
