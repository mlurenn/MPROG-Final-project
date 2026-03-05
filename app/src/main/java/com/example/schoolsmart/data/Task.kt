package com.example.schoolsmart.data

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val dueDate: Long,
    val status: TaskStatus,
    val category: TaskCategory,
    val smsEnabled: Boolean = false,
    val reminderEnabled: Boolean = false,
    val links: List<String> = emptyList(),
    val pictures: List<String> = emptyList()
)

enum class TaskStatus{
    TODO,
    IN_PROGRESS,
    DONE
}

enum class TaskCategory{
    EXAM,
    ASSIGNMENT,
    LECTURE
}
