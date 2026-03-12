package com.example.schoolsmart.ui.dialogs

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.schoolsmart.data.TaskCategory
import com.example.schoolsmart.notifications.scheduleReminder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddTaskDialog(
    title: String,
    description: String,
    dueDate: Long,
    selectedCategory: String,
    reminderEnabled: Boolean,

    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateChange: (Long) -> Unit,
    onCategoryClick: (TaskCategory) -> Unit,

    onReminderChange: (Boolean) -> Unit,

    onConfirm: () -> String,
    onDismiss: () -> Unit
){
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var isExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = { Text("Create new task") },

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

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Category")
                    Spacer(modifier = Modifier.width(8.dp))
                    Box {
                        Button(onClick = { isExpanded = true }) {
                            Text(formatEnumText(selectedCategory))
                        }

                        DropdownMenu(
                            expanded = isExpanded,
                            onDismissRequest = { isExpanded = false }
                        ) {

                            DropdownMenuItem(
                                text = { Text("Lecture") },
                                onClick = {
                                    onCategoryClick(TaskCategory.LECTURE)
                                    isExpanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Assignment") },
                                onClick = {
                                    onCategoryClick(TaskCategory.ASSIGNMENT)
                                    isExpanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Exam") },
                                onClick = {
                                    onCategoryClick(TaskCategory.EXAM)
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
                        checked = reminderEnabled,
                        onCheckedChange = onReminderChange
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
                val newTasksID = onConfirm()

                if(reminderEnabled){
                    scheduleReminder(
                        context,
                        newTasksID,
                        title,
                        dueDate
                    )
                }

            }) {
                Text("Add")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}