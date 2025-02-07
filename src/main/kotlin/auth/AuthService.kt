package com.todo.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtConfig {
    private const val secret = "my-awesome-sercret"
    private const val issuer = "com.todo"
    private const val audience = "com.todo.users"
    private const val expiration = 24 * 60 * 60 * 1000 // 1 day
    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(username: String): String {
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("username", username)
            .withExpiresAt(Date(System.currentTimeMillis() + expiration))
            .sign(algorithm)
    }

    val verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()
}