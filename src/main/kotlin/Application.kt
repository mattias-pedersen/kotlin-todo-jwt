package com.todo

import com.todo.auth.JwtConfig
import com.todo.database.DatabaseFactory
import com.todo.database.Migrations
import com.todo.service.TaskService
import com.todo.service.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // Init the database
    DatabaseFactory.init()

    // run migrations
    Migrations.run()

    val userService = UserService()
    val taskService = TaskService(userService)

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "todo-app"
            verifier(JwtConfig.verifier)
            validate { credential ->
                val username = credential.payload.getClaim("username").asString()
                if (!username.isNullOrBlank()) JWTPrincipal(credential.payload) else null
            }
        }
    }


    routing {
        authRoutes(userService)
        taskRoutes(taskService)

        authenticate("auth-jwt") {
            get("/protected") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("Hello ${principal.name}")
            }
        }
    }

    configureSerialization()
    configureRouting()
}
