package com.todo.service

import com.todo.database.Users
import com.todo.models.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class UserService {

    // Register users
    fun registerUser(username: String, password: String): Boolean = transaction {
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())

        val existingUser = Users.select { Users.username eq username }.singleOrNull()
        if (existingUser != null) return@transaction false

        Users.insert {
            it[Users.username] = username
            it[Users.password] = hashedPassword
        }
        true
    }


    // Authenticate user
    fun authenticate(username: String, password: String): User? = transaction{
        val userRow = Users.select { Users.username eq username }.singleOrNull() ?: return@transaction null
        val hashedPassword = userRow[Users.password]

        if (BCrypt.checkpw(password, hashedPassword)) {
            User(
                id = userRow[Users.id],
                username = userRow[Users.username],
                password = hashedPassword
            )
        } else {
            null
        }
    }

    // Get userid by username
    fun getUserIdByUsername(username: String): Int? = transaction {
        Users.select { Users.username eq username }
            .map { it[Users.id] }
            .singleOrNull()
    }

    private fun rowToTask(row: ResultRow): User {
        return User(
            id = row[Users.id],
            username = row[Users.username],
            password = row[Users.password],
        )
    }
}