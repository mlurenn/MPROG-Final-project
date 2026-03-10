package com.example.schoolsmart.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.schoolsmart.data.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Represents a task for the user as a Card
@Composable
fun TaskCard(task: Task, onClick: (Task) -> Unit) {

    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = formatter.format(Date(task.dueDate))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = { onClick(task) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = task.title)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = formattedDate)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = task.description)
            Spacer(modifier = Modifier.height(8.dp))

            val displayStatus = task.status.name.replace("_", " ")

            Text(text = "Status: $displayStatus")
        }
    }
}