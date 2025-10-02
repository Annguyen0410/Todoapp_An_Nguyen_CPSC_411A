package com.annguyen.todoappp

/**
 * Data class representing a single to-do item.
 * 
 * @property id Unique identifier for the item
 * @property text The task description
 * @property isCompleted Whether the task is completed or not
 */
data class TodoItem(
    val id: Int,
    val text: String,
    val isCompleted: Boolean = false
)


