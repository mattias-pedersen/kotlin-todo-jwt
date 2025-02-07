package com.todo

import com.todo.models.Task
import com.todo.service.TaskService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {

        get("/") {
            call.respondText("just a shitty todo api.")
        }

    }
}
