package com.module.springboot.service.account.starter.service

import com.module.springboot.service.account.starter.config.ServiceAccountSecurityProperties
import com.module.springboot.service.account.starter.view.ServiceAccount
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import java.util.*


class ServiceAccountJwtService(
    private val properties: ServiceAccountSecurityProperties,
) {
    lateinit var secret: ByteArray
    @PostConstruct
    fun postConstruct() {
        secret = Base64.getDecoder().decode(properties.secret)
    }

    fun parseToken(token: String): ServiceAccount? {
        val secretKey = Keys.hmacShaKeyFor(secret)
        val claims: Claims
        try {
            claims =  Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload
        } catch (e: JwtException) {
            return null
        }
        val newClaim = claims.mapValues { it.value }
        return ServiceAccount(
            clientId = newClaim["clientId"] as String,
            name = newClaim["name"] as String,
            scopes = (newClaim["scopes"] as List<*>).map { it.toString() }.toMutableSet(),
        )
    }

    fun isValidToken(token: String): Boolean {
        val secretKey = Keys.hmacShaKeyFor(secret)
        return try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload
            true
        } catch (e: JwtException) {
            false
        }
    }

    fun issueToken(claims: ServiceAccount, expireAtNumberOfSeconds: Int): String {
        val mapClaims = mutableMapOf(
            "clientId" to claims.clientId,
            "name" to claims.name,
            "scopes" to claims.scopes,
        )

        val signedKey = Keys.hmacShaKeyFor(secret)
        val jwtBuilder = Jwts.builder()
            .issuer(properties.issuer)
            .subject(mapClaims.getOrDefault("id", UUID.randomUUID()).toString())
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expireAtNumberOfSeconds.toLong() * 1000))
            .signWith(signedKey)

        // Add all claims to the token
        mapClaims.forEach { (key, value) ->
            jwtBuilder.claim(key, value)
        }
        return jwtBuilder.compact()
    }
}