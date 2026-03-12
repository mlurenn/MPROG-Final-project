package com.example.schoolsmart.ui.dialogs

import android.icu.util.Calendar
import android.widget.CalendarView
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.room.util.TableInfo
import com.example.schoolsmart.data.Task
import com.example.schoolsmart.data.TaskStatus

@Composable
fun CalendarDialog(
    tasks: List<Task>,
    onDismiss: () -> Unit
){
    val today = java.util.Calendar.getInstance()
    var selectedTasks by remember {
        mutableStateOf(
            getTasksForDate(
                tasks,
                today.get(java.util.Calendar.YEAR),
                today.get(java.util.Calendar.MONTH),
                today.get(java.util.Calendar.DAY_OF_MONTH)
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select a date") },
        text = {
            Column() {
                AndroidView(
                    factory = { context ->
                        CalendarView(context).apply {
                            setOnDateChangeListener { _, year, month, dayOfMonth ->
                                selectedTasks = getTasksForDate(tasks, year, month, dayOfMonth)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if(selectedTasks.isNotEmpty()){
                    Column(){
                        Text(
                            text = "Tasks due this date:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        selectedTasks.forEach { task ->

                            val status = task.status.toString()
                            val formattedStatus = status.replace("_", " ")
                                .lowercase().replaceFirstChar { it.uppercase() }

                            Text(
                                text = "- ${task.title} - $formattedStatus",
                                fontSize = 16.sp
                            )
                        }
                    }
                }else{
                    Text("No tasks due this date")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

fun getTasksForDate(tasks: List<Task>, year: Int, month: Int, day: Int): List<Task> {
    return tasks.filter { task ->
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = task.dueDate
        cal.get(java.util.Calendar.YEAR) == year &&
                cal.get(java.util.Calendar.MONTH) == month &&
                cal.get(java.util.Calendar.DAY_OF_MONTH) == day
    }
}