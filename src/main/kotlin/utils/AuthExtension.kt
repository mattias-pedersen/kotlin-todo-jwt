package com.todo.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

suspend fun ApplicationCall.getAuthenticatedUsername(): String? {
    val principal = principal<JWTPrincipal>() ?: run {
        respond(HttpStatusCode.Unauthorized, "Invalid or missing token")
        return null
    }

    val username = principal.payload.getClaim("username").asString()
    if (username.isNullOrBlank()) {
        respond(HttpStatusCode.Unauthorized, "Invalid token")
        return null
    }

    return username
}