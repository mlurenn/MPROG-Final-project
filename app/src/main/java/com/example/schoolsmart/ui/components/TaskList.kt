package com.example.schoolsmart.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.schoolsmart.data.Task

// Displays a list of tasks in a scrollable column
@Composable
fun TaskList(tasks: List<Task>, modifier: Modifier = Modifier, onTaskClick: (Task) -> Unit) {
    LazyColumn(modifier = modifier.padding(8.dp)) {
        items(tasks) { task ->
            TaskCard(task, onClick = onTaskClick)
        }
    }
}