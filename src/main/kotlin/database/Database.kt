package com.todo.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcUrl = "jdbc:postgresql://localhost:5432/kotlinTodo"
        val user = "postgres"
        val password = "6i28dns6"

        Database.connect(jdbcUrl, driverClassName, user, password)
    }
}

// Task table
object Tasks : Table("tasks") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val description = text("description")
    val completed = bool("completed").default(false)
    val userId = integer("user_id").references(Users.id)
    override val primaryKey = PrimaryKey(id)
}

// Users table
object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 255)
    val password = varchar("password", 255)
    override val primaryKey = PrimaryKey(id)
}