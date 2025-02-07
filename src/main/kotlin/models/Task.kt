package com.todo.models

import kotlinx.serialization.Serializable


@Serializable
data class Task(
    val id: Int? = null,
    val title: String,
    val description: String,
    val completed: Boolean = false,
    val userId: Int? = null
)

@Serializable
data class UpdateCompletionRequest(
    val completed: Boolean
)