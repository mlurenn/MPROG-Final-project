package com.example.schoolsmart.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "tasks")
@TypeConverters(Converters::class)
data class Task(

    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val dueDate: Long,
    val status: TaskStatus,
    val category: TaskCategory,
    val smsEnabled: Boolean = false,
    val reminderEnabled: Boolean = false,
    var links: List<String> = emptyList(),
    val pictures: List<String> = emptyList()
)

enum class TaskStatus{
    TODO,
    IN_PROGRESS,
    DONE,
}

enum class TaskCategory{
    EXAM,
    ASSIGNMENT,
    LECTURE,
}
