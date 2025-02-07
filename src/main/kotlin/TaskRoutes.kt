package com.todo

import com.todo.models.Task
import com.todo.models.UpdateCompletionRequest
import com.todo.service.TaskService
import com.todo.utils.getAuthenticatedUsername
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskRoutes(taskService: TaskService) {
    authenticate("auth-jwt") {
        route("/tasks") {

            get {
                // Get principal and check if not null
                //val principal = call.principal<JWTPrincipal>()
                //if (principal == null) {
                //    call.respond(HttpStatusCode.Unauthorized, "Invalid or missing token")
                //    return@get
                //}
//
                //// Get username from payload
                //val username = principal.payload.getClaim("username").asString()
                //if (username.isNullOrBlank()) {
                //    call.respond(HttpStatusCode.Unauthorized, "Invalid token")
                //    return@get
                //}

                val username = call.getAuthenticatedUsername() ?: return@get
                println("isername: $username")
                val tasks = taskService.getAllTasks(username)
                call.respond(tasks)
            }

            get("{id}") {
                val username = call.getAuthenticatedUsername() ?: return@get

                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid id")

                val task = taskService.getTaskById(id, username)
                if (task != null) {
                    call.respond(task)
                } else {
                    call.respondText("Task not found", status = HttpStatusCode.NotFound)
                }
            }

            post {
                try {
                    val username = call.getAuthenticatedUsername() ?: return@post
                    val task = call.receive<Task>()
                    println("task: $task")
                    val taskId = taskService.createTask(task, username)
                    call.respond(mapOf("id" to taskId))
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }

            put("{id}") {
                val username = call.getAuthenticatedUsername() ?: return@put
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID")

                val task = call.receive<Task>()
                if (taskService.updateTask(id, task, username)) {
                    call.respondText("Task updated successfully", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Task not found", status = HttpStatusCode.NotFound)
                }
            }

            patch("{id}/complete") {
                val username = call.getAuthenticatedUsername() ?: return@patch
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid ID")

                val request = call.receive<UpdateCompletionRequest>()
                if (taskService.updateTaskCompletion(id, request.completed, username)) {
                    call.respondText("Task updated successfully", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Task not found", status = HttpStatusCode.NotFound)
                }
            }
        }
    }
}