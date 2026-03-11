package com.example.schoolsmart.ui.dialogs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.schoolsmart.data.Task
import com.example.schoolsmart.data.TaskCategory
import com.example.schoolsmart.data.TaskDatabase
import com.example.schoolsmart.data.TaskStatus
import com.example.schoolsmart.data.TaskViewModel
import com.example.schoolsmart.notifications.scheduleReminder
import com.example.schoolsmart.notifications.sendNotification
import com.example.schoolsmart.ui.screens.PhotosActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditTaskDialog(
    task: Task,

    title: String,
    description: String,
    dueDate: Long,
    selectedCategory: String,
    selectedStatus: String,
    smsEnabled: Boolean,
    reminderEnabled: Boolean,

    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateChange: (Long) -> Unit,
    onCategoryClick: (TaskCategory) -> Unit,
    onStatusClick: (TaskStatus) -> Unit,

    onSmsChange: (Boolean) -> Unit,
    onReminderChange: (Boolean) -> Unit,

    onConfirm:  () -> Unit,
    onDismiss:  () -> Unit,
    onDelete:   () -> Unit
){
    val context = LocalContext.current
    val viewModel: TaskViewModel = viewModel()

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var categoryExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    var links by remember { mutableStateOf(task.links)}
    var newLink by remember(task) { mutableStateOf("")}

    AlertDialog(
        onDismissRequest = onDismiss,

        title = { Text("Task Overview") },

        // Title & description
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Due date
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Due date")
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val calendar = java.util.Calendar.getInstance()
                        calendar.timeInMillis = dueDate

                        android.app.DatePickerDialog(
                            context, { _, year, month, dayOfMonth ->
                                val newCalendar = java.util.Calendar.getInstance()
                                newCalendar.set(year, month, dayOfMonth)
                                onDateChange(newCalendar.timeInMillis)
                            },
                            calendar.get(java.util.Calendar.YEAR),
                            calendar.get(java.util.Calendar.MONTH),
                            calendar.get(java.util.Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Text(dateFormatter.format(Date(dueDate)))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category & Status
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category
                    Text("Category")
                    Spacer(modifier = Modifier.width(8.dp))
                    Box {
                        Button(onClick = { categoryExpanded = true }) {
                            Text(selectedCategory)
                        }

                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {

                            DropdownMenuItem(
                                text = { Text("Lecture") },
                                onClick = {
                                    onCategoryClick(TaskCategory.LECTURE)
                                    categoryExpanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Assignment") },
                                onClick = {
                                    onCategoryClick(TaskCategory.ASSIGNMENT)
                                    categoryExpanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Exam") },
                                onClick = {
                                    onCategoryClick(TaskCategory.EXAM)
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // Status
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Status")
                    Spacer(modifier = Modifier.width(24.dp))

                    Box {
                        Button(onClick = { statusExpanded = true }) {
                            Text(selectedStatus.replace("_", " "))
                        }

                        DropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false }
                        ) {

                            DropdownMenuItem(
                                text = { Text("To-Do") },
                                onClick = {
                                    onStatusClick(TaskStatus.TODO)
                                    statusExpanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("In progress") },
                                onClick = {
                                    onStatusClick(TaskStatus.IN_PROGRESS)
                                    statusExpanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Done") },
                                onClick = {
                                    onStatusClick(TaskStatus.DONE)
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

                // Reminders
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = smsEnabled,
                        onCheckedChange = onSmsChange
                    )
                    Text("Enable SMS reminder")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = reminderEnabled,
                        onCheckedChange = onReminderChange
                    )
                    Text("Enable notification reminder")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Photos
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Add or show photos")
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val intent = Intent(context, PhotosActivity::class.java)
                        intent.putExtra("TASK_ID", task.id)
                        context.startActivity(intent)

                    }){Text("Photos")}
                }

                // Links
                Text("Links")
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newLink,
                        onValueChange = {newLink = it},
                        label = { Text("URL") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(onClick = {
                        val trimmedLink = newLink.trim()
                        if (trimmedLink.isNotEmpty() && !links.contains(trimmedLink)) {
                            links = links + trimmedLink
                            task.links = links
                            newLink = ""
                        }
                    }){Text("Add")}
                }

                Spacer(modifier = Modifier.height(4.dp))

                if(task.links.isNotEmpty()){
                    Column{
                        links.filter { it.isNotBlank() }
                            .forEach { link ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = { openLink(context, link) }
                                    ) {
                                        Text(link)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    TextButton(
                                        onClick = {
                                            links = links - link
                                            task.links = links
                                        }
                                    ) {
                                        Text("Remove")
                                    }
                                }
                            }
                    }
                }
            }
        },

        confirmButton = {
            TextButton(onClick = {
                if (title.isBlank()) {
                    Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                    return@TextButton
                }

                if(reminderEnabled){
                    scheduleReminder(
                        context,
                        task.id,
                        title,
                        dueDate,
                    )
                }

                viewModel.updateTaskWithLatestPictures(
                    taskId = task.id,
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    category = TaskCategory.valueOf(selectedCategory.uppercase()),
                    status = TaskStatus.valueOf(selectedStatus.replace(" ", "_").uppercase()),
                    sms = smsEnabled,
                    reminder = reminderEnabled
                )

                onConfirm()

            }) {
                Text("Done")
            }
        },

        dismissButton = {
            TextButton(onClick = {
                onDelete()
            }) {
                Text("DELETE")
            }
        },
    )
}

fun openLink(context: Context, url: String){

    var safeUrl = ""
    if(url.startsWith("https://") || url.startsWith("http://")){
        safeUrl = url
    } else{
        safeUrl = "https://$url"
    }

    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(safeUrl)
    context.startActivity(intent)
}