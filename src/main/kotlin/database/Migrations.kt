package com.todo.database

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object Migrations {
    fun run() {
        transaction{
            SchemaUtils.create(Tasks)
            SchemaUtils.create(Users)
        }
    }
}