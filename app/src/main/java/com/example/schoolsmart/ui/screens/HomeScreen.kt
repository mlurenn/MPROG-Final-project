package com.example.schoolsmart.ui.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import java.util.UUID

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
    val context = LocalContext.current

    var tasks by remember { mutableStateOf(sampleTasks()) }

    val sortedTasks = tasks.sortedBy { it.dueDate }
    val todoTasks = sortedTasks.filter { it.status == TaskStatus.TODO }
    val inProgressTasks = sortedTasks.filter { it.status == TaskStatus.IN_PROGRESS }
    val doneTasks = sortedTasks.filter { it.status == TaskStatus.DONE }

    var currentFilter by remember { mutableStateOf("all") }

    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

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

        TaskList(
            tasks = filteredTasks,
            modifier = Modifier.weight(1f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {showDialog = true}) { Text("Add Task") }
        }

        var isExpanded by remember { mutableStateOf(false) }
        var selectedCategory by remember { mutableStateOf(TaskCategory.LECTURE) }

        var smsEnabled by remember { mutableStateOf(false) }
        var reminderEnabled by remember { mutableStateOf(false) }

        if(showDialog){
            AlertDialog(
                onDismissRequest = {showDialog = false},

                title = { Text("Create new task") },

                text = {
                    Column {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Category")
                            Spacer(modifier = Modifier.width(6.dp))
                            Box {
                                Button(onClick = { isExpanded = true }) {
                                    Text(selectedCategory.toString())
                                }

                                DropdownMenu(
                                    expanded = isExpanded,
                                    onDismissRequest = { isExpanded = false }
                                ) {

                                    DropdownMenuItem(
                                        text = { Text("Lecture") },
                                        onClick = {
                                            selectedCategory = TaskCategory.LECTURE
                                            isExpanded = false
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = { Text("Assignment") },
                                        onClick = {
                                            selectedCategory = TaskCategory.ASSIGNMENT
                                            isExpanded = false
                                        }
                                    )

                                    DropdownMenuItem(
                                        text = { Text("Exam") },
                                        onClick = {
                                            selectedCategory = TaskCategory.EXAM
                                            isExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = smsEnabled,
                                onCheckedChange = { smsEnabled = it }
                            )
                            Text("Enable SMS reminder")
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = reminderEnabled,
                                onCheckedChange = { reminderEnabled = it }
                            )
                            Text("Enable notification reminder")
                        }
                    }
                },

                confirmButton = {
                    TextButton(onClick = {
                        if (title.isBlank()) {
                            Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }

                        val newTask = addTask(
                            title = title,
                            desc = description,
                            dueDate = System.currentTimeMillis(),
                            category = selectedCategory,
                            smsEnabled = smsEnabled,
                            reminderEnabled = reminderEnabled,
                        )

                        tasks = tasks + newTask

                        title = ""
                        description = ""
                        smsEnabled = false
                        reminderEnabled = false
                        showDialog = false

                        Toast.makeText(context, "Task created", Toast.LENGTH_SHORT).show()

                    }) {
                        Text("Add")
                    }
                },

                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
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

fun addTask(
    title: String,
    desc: String,
    dueDate: Long,
    category: TaskCategory,
    smsEnabled: Boolean,
    reminderEnabled: Boolean,
): Task {
    return Task(
        id = UUID.randomUUID().toString(),
        title = title,
        description = desc,
        dueDate = dueDate,
        status = TaskStatus.TODO,
        category = category,
        smsEnabled = smsEnabled,
        reminderEnabled = reminderEnabled,
        links = emptyList(),
        pictures = emptyList()
    )
}

fun deleteTask(task: Task){

}

fun editTask(task: Task){

}
