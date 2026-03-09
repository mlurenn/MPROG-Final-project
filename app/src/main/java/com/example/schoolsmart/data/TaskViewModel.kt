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
}