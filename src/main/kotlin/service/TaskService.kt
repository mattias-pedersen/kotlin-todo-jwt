package com.todo.service

import com.todo.database.Tasks
import com.todo.database.Users
import com.todo.models.Task
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class TaskService(private val userService: UserService) {

    fun getAllTasks(username: String) : List<Task> = transaction {
        val userId = userService.getUserIdByUsername(username) ?: return@transaction emptyList()
        println("userid: $userId")
        Tasks.select { Tasks.userId eq userId }
        .map { rowToTask(it) }
    //Tasks.selectAll().map { rowToTask(it) }
    }

    fun getTaskById(id: Int, username: String): Task? = transaction {
        val userId = userService.getUserIdByUsername(username) ?: return@transaction null
        Tasks.select { (Tasks.id eq id) and (Tasks.userId eq userId) }
            .singleOrNull()
            ?.let { rowToTask(it) }
    //Tasks.select { Tasks.id eq id }.singleOrNull()?.let { rowToTask(it) }
    }

    fun createTask(task: Task, username: String): Int = transaction {
        val usernameId = userService.getUserIdByUsername(username) ?: throw IllegalArgumentException("User not found")
        println("userId: $usernameId")
        Tasks.insert {
            it[title] = task.title
            it[description] = task.description
            it[completed] = task.completed
            it[userId] = usernameId
        }[Tasks.id]
    }

    fun updateTask(id: Int, task: Task, username: String): Boolean = transaction {
        val userId = userService.getUserIdByUsername(username) ?: return@transaction false
        Tasks.update({ (Tasks.id eq id) and (Tasks.userId eq userId) }) {
            it[title] = task.title
            it[description] = task.description
            it[completed] = task.completed
        } > 0
    }

    fun updateTaskCompletion(id: Int, completed: Boolean, username: String): Boolean = transaction {
        val userId = userService.getUserIdByUsername(username) ?: return@transaction false
        val updateStatement = Tasks.update({ (Tasks.id eq id) and (Tasks.userId eq userId) }) {
            it[Tasks.completed] = completed
        }
        updateStatement > 0
    }



    private fun rowToTask(row: ResultRow): Task {
        return Task(
            id = row[Tasks.id],
            title = row[Tasks.title],
            description = row[Tasks.description],
            completed = row[Tasks.completed],
            userId = row[Tasks.userId]
        )
    }
}