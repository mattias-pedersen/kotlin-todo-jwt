package com.todo

import com.todo.auth.JwtConfig
import com.todo.service.UserService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(val username: String, val password: String)

@Serializable
data class AuthResponse(val accessToken: String)

fun Route.authRoutes(userService: UserService) {
    post("/register") {

        try {
            val request = call.receive<AuthRequest>()
            val success = userService.registerUser(request.username, request.password)

            if (success) {
                call.respond(HttpStatusCode.Created, "User register successfully!")
            } else {
                call.respond(HttpStatusCode.Conflict, "Username already exists")
            }
        } catch (e: Exception) {
            println("error: ${e.message}")
            call.respond(HttpStatusCode.BadRequest, "Invalid request body")
        }
    }

    post("/login") {
        val request = call.receive<AuthRequest>()
        val user = userService.authenticate(request.username, request.password)

        if (user != null) {
            val token = JwtConfig.generateToken(user.username)
            call.respond(HttpStatusCode.OK, AuthResponse(token))
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
        }
    }
}