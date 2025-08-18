# Spring Boot Service Account Library

這是一個簡單的 Spring Boot library，提供基於 JWT 的服務帳戶認證功能。

## 功能特色

- JWT token 驗證與解析
- Service Account 認證
- Spring Security 整合
- 可配置的保護/非保護路由
- Scope 權限管理

## 如何使用

### 1. 添加 JAR 檔案到您的專案

將 `build/libs/spring-boot-service-account-starter-0.0.1-SNAPSHOT.jar` 複製到您的專案中，並加入到 classpath。

### 2. 在您的 Spring Boot 應用程式中引入配置

```kotlin
@SpringBootApplication
@Import(ServiceAccountSecurityConfiguration::class)
class YourApplication

fun main(args: Array<String>) {
    runApplication<YourApplication>(*args)
}
```

### 3. 配置 application.properties

```properties
# JWT 配置
jwt.secret=your-base64-encoded-secret-key

# 安全配置
security.unprotected-routes[0]=/public/**
security.unprotected-routes[1]=/health
security.unprotected-routes[2]=/actuator/**
security.identity-key=jwt
```

### 4. 在您的 Controller 中使用

```kotlin
@RestController
class YourController {

    @GetMapping("/protected")
    fun protectedEndpoint(authentication: Authentication): ResponseEntity<String> {
        val serviceAccount = authentication.principal as ServiceAccount
        return ResponseEntity.ok("Hello ${serviceAccount.name}")
    }

    @GetMapping("/require-scope")
    @RequireScope(scope = "read:data")
    fun requireScopeEndpoint(): ResponseEntity<String> {
        return ResponseEntity.ok("You have the required scope!")
    }
}
```

## 主要類別說明

### ServiceAccountFilter
- 處理 JWT token 驗證
- 從 Authorization header 或 Cookie 中提取 token
- 設置 Spring Security 認證上下文

### ServiceAccount
- 表示服務帳戶的資料類別
- 包含 clientId, name, scopes

### ServiceAccountJwtService
- JWT token 的解析與驗證服務
- 轉換 JWT claims 為 ServiceAccount 物件

### @RequireScope
- 方法級別的權限控制註解
- 檢查是否具有特定 scope

## 認證方式

### Bearer Token (推薦)
```
Authorization: Bearer your-jwt-token
```

### Cookie
```
Cookie: jwt=your-jwt-token
```

## 建構

```bash
./gradlew clean jar
```

生成的 JAR 檔案位於 `build/libs/` 目錄中。
