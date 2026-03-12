package com.example.schoolsmart.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = TaskDatabase.getDatabase(app).taskDao()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks = _tasks.asStateFlow()


    init {
        loadTasks()
    }

    fun loadTasks(){
        viewModelScope.launch {
            _tasks.value = dao.getAllTasks()
        }
    }

    fun addTask(task: Task){
        viewModelScope.launch {
            dao.insertTask(task)
            loadTasks()
        }
    }

    fun deleteTask(task: Task){
        viewModelScope.launch {
            dao.deleteTask(task)
            loadTasks()
        }
    }

    fun updateTask(task: Task){
        viewModelScope.launch {
            dao.updateTask(task)
            loadTasks()
        }
    }

    fun updateTaskWithLatestPictures(
        taskId: String,
        title: String,
        description: String,
        dueDate: Long,
        category: TaskCategory,
        status: TaskStatus,
        reminder: Boolean,
        links: List<String>
    ) = viewModelScope.launch {
        val task = dao.getTaskById(taskId)
        val updatedTask = task.copy(
            title = title,
            description = description,
            dueDate = dueDate,
            category = category,
            status = status,
            reminderEnabled = reminder,
            links = links,
            pictures = task.pictures
        )
        dao.updateTask(updatedTask)
    }
}