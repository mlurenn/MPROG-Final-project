package com.example.schoolsmart.ui.screens

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.schoolsmart.data.Task
import com.example.schoolsmart.data.TaskCategory
import com.example.schoolsmart.data.TaskStatus
import androidx.compose.material3.Button
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.schoolsmart.data.TaskViewModel
import java.util.UUID
import com.example.schoolsmart.ui.components.TaskList
import com.example.schoolsmart.notifications.notificationSetup
import com.example.schoolsmart.notifications.sendNotification
import com.example.schoolsmart.ui.dialogs.AddTaskDialog
import com.example.schoolsmart.ui.dialogs.EditTaskDialog

class HomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        notificationSetup(this)

        setContent {
            TasksScreen()
        }
    }
}

// Displays and filters tasks
@Composable
fun TasksScreen(){
    val context = LocalContext.current
    val viewModel: TaskViewModel = viewModel()
    val tasks by viewModel.tasks.collectAsState()

    val sortedTasks = tasks.sortedBy { it.dueDate }
    val todoTasks = sortedTasks.filter { it.status == TaskStatus.TODO }
    val inProgressTasks = sortedTasks.filter { it.status == TaskStatus.IN_PROGRESS }
    val doneTasks = sortedTasks.filter { it.status == TaskStatus.DONE }

    var currentFilter by remember { mutableStateOf("all") }

    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(System.currentTimeMillis()) }

    var editingTask by remember { mutableStateOf<Task?>(null) }

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
            modifier = Modifier.weight(1f),
            onTaskClick = { task ->
                editingTask = task
            }
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

        // Add Task Dialog
        if(showDialog) {
            AddTaskDialog(
                title = title,
                description = description,
                dueDate = dueDate,
                selectedCategory = selectedCategory.toString(),
                smsEnabled = smsEnabled,
                reminderEnabled = reminderEnabled,

                onTitleChange = { title = it },
                onDescriptionChange = { description = it },
                onDateChange = { dueDate = it },
                onCategoryClick = { selectedCategory = it },

                onSmsChange = { smsEnabled = it },
                onReminderChange = { reminderEnabled = it },

                onDismiss = { showDialog = false },

                onConfirm = {
                    val newTask = addTask(
                        title = title,
                        desc = description,
                        dueDate = dueDate,
                        category = selectedCategory,
                        smsEnabled = smsEnabled,
                        reminderEnabled = reminderEnabled,
                    )
                    viewModel.addTask(newTask)

                    val newTasksID = newTask.id

                    title = ""
                    description = ""
                    dueDate = System.currentTimeMillis()
                    selectedCategory = TaskCategory.LECTURE
                    smsEnabled = false
                    reminderEnabled = false
                    showDialog = false

                    Toast.makeText(context, "Task added", Toast.LENGTH_SHORT).show()

                    newTasksID
                })
        }

        // Edit Task Dialog
        if (editingTask != null) {
            EditTaskDialog(
                task = editingTask!!,
                title = editingTask!!.title,
                description = editingTask!!.description,
                dueDate = editingTask!!.dueDate,
                selectedCategory = editingTask!!.category.toString(),
                selectedStatus = editingTask!!.status.toString(),
                smsEnabled = editingTask!!.smsEnabled,
                reminderEnabled = editingTask!!.reminderEnabled,
                onTitleChange = { editingTask = editingTask!!.copy(title = it) },
                onDescriptionChange = { editingTask = editingTask!!.copy(description = it) },
                onDateChange = { editingTask = editingTask!!.copy(dueDate = it) },
                onCategoryClick = { editingTask = editingTask!!.copy(category = it) },
                onStatusClick = { editingTask = editingTask!!.copy(status = it) },
                onSmsChange = { editingTask = editingTask!!.copy(smsEnabled = it) },
                onReminderChange = { editingTask = editingTask!!.copy(reminderEnabled = it) },
                onConfirm = {
                    viewModel.updateTask(editingTask!!)
                    editingTask = null
                },
                onDismiss = { editingTask = null },

                onDelete = {
                    viewModel.deleteTask(editingTask!!)
                    editingTask = null
                }
            )
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
