package com.module.springboot.service.account.starter.service

import com.module.springboot.service.account.starter.config.SecurityProperties
import com.module.springboot.service.account.starter.view.ServiceAccount
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.util.*


@Service("service-account")
class ServiceAccountJwtService(
    private val properties: SecurityProperties,
    private val defaultJwtService: DefaultJwtService,
): JwtService<ServiceAccount>, TokenIssuer<ServiceAccount> {
    override lateinit var secret: ByteArray
    @PostConstruct
    fun postConstruct() {
        secret = Base64.getDecoder().decode(properties.secret)
    }

    override fun parseToken(token: String): ServiceAccount? {
        val claims = defaultJwtService.parseToken(token) ?: return null
        return ServiceAccount(
            clientId = claims["clientId"] as String,
            name = claims["name"] as String,
            scopes = (claims["scopes"] as List<*>).map { it.toString() }.toMutableSet(),
        )
    }

    override fun isValidToken(token: String): Boolean {
        return defaultJwtService.isValidToken(token)
    }

    override fun issueToken(claims: ServiceAccount, expireAtNumberOfSeconds: Int): String {
        val mapClaims = mutableMapOf(
            "clientId" to claims.clientId,
            "name" to claims.name,
            "scopes" to claims.scopes,
        )
        return defaultJwtService.issueToken(mapClaims, expireAtNumberOfSeconds)
    }
}