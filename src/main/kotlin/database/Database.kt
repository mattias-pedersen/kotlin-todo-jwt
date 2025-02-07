package com.todo.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import io.github.cdimascio.dotenv.dotenv

object DatabaseFactory {
    fun init() {
        val dotenv = dotenv()
        val driverClassName = dotenv["DB_DRIVER"]
        val jdbcUrl = dotenv["DB_URL"]
        val user = dotenv["DB_USER"]
        val password = dotenv["DB_PASSWORD"]

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