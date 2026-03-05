package com.example.schoolsmart.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.schoolsmart.data.Task
import com.example.schoolsmart.data.TaskCategory
import com.example.schoolsmart.data.TaskStatus
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

class HomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TasksScreen()
        }
    }
}

// Dummy Data
fun sampleTasks(): List<Task> {
    return listOf(
        Task(
            id = "1",
            title = "Mattelektion",
            description = "Kapitel 1-3",
            dueDate = System.currentTimeMillis(),
            status = TaskStatus.TODO,
            category = TaskCategory.LECTURE
        ),
        Task(
            id = "2",
            title = "Historiauppgift",
            description = "Skriv om WW2",
            dueDate = System.currentTimeMillis(),
            status = TaskStatus.IN_PROGRESS,
            category = TaskCategory.ASSIGNMENT
        ),
        Task(
            id = "3",
            title = "Kemiexamen",
            description = "Förbered labbrapporter",
            dueDate = System.currentTimeMillis(),
            status = TaskStatus.TODO,
            category = TaskCategory.EXAM
        )
    )
}

// Displays and filters tasks
@Composable
fun TasksScreen(){
    val sortedTasks = sampleTasks().sortedBy { it.dueDate }
    val todoTasks = sortedTasks.filter { it.status == TaskStatus.TODO }
    val inProgressTasks = sortedTasks.filter { it.status == TaskStatus.IN_PROGRESS }
    val doneTasks = sortedTasks.filter { it.status == TaskStatus.DONE }

    var currentFilter by remember { mutableStateOf("all") }

    val filteredTasks = when (currentFilter) {
        "todo" -> todoTasks
        "inProgress" -> inProgressTasks
        "done" -> doneTasks
        else -> sortedTasks
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(
            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        )) {

        Text(
            text = "All Tasks",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ){
            Button(onClick = {currentFilter = "all"}) { Text("All") }
            Button(onClick = {currentFilter = "todo"}) { Text("To-Do") }
            Button(onClick = {currentFilter = "inProgress"}) { Text("In Progress") }
            Button(onClick = {currentFilter = "done"}) { Text("Done") }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TaskList(tasks = filteredTasks)

        Button(onClick = {
            //CreateTaskDialog() Show a pop-up window which allows the user to create a new task
        }) { Text("Add Task") }
    }
}

// Displays a list of tasks in a scrollable column
@Composable
fun TaskList(tasks: List<Task>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.padding(8.dp)) {
        items(tasks) { task ->
            TaskCard(task)
        }
    }
}

// Represents a task for the user as a Card
@Composable
fun TaskCard(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.title)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = task.description)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Status: ${task.status}")
        }
    }
}


// --- Functions handling tasks ---

fun AddTask(title: Task.title, desc: Task.description, dueDate: Task.dueDate, status: TaskStatus,  category: TaskCategory, smsEnabled: Boolean, reminderEnabled: Boolean, links: List<String>, pictures: List<String>){
    //create unique id
    //continue with saving
}

fun SaveTask(task: Task){

}

fun DeleteTask(task: Task){

}

fun EditTask(task: Task){

}
