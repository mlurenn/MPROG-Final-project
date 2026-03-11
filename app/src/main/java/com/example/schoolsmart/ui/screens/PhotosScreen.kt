package com.example.schoolsmart.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.schoolsmart.data.TaskDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.sp

class PhotosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val taskId = intent.getStringExtra("TASK_ID") ?: return finish()

        setContent {
            PhotosScreen(taskId, onBack = { finish() })
        }
    }
}

@Composable
fun PhotosScreen(taskID: String, onBack: () -> Unit){
    val context = LocalContext.current
    val database = TaskDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    var images by remember { mutableStateOf(listOf<Pair<Bitmap, String>>())}

    LaunchedEffect(taskID) {
        val task = database.taskDao().getTaskById(taskID)
        val bitmaps = task.pictures.mapNotNull { path ->
            val file = File(path)
            val b =
                if(file.exists())
                    BitmapFactory.decodeFile(path)
                else
                    null
            b?.let {it to path}
        }
        images = bitmaps
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ){bitmap ->
        bitmap?.let {

            val path = savePhoto(context, it)

            scope.launch(Dispatchers.IO) {
                val task = database.taskDao().getTaskById(taskID)
                val updatedImageList = task.pictures + path
                val updatedTask = task.copy(pictures = updatedImageList)
                database.taskDao().updateTask(updatedTask)
            }

            images = listOf(it to path) + images
        }
    }
    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(images.isEmpty()){
                Text(
                    text = "Take a photo of your notes or the whiteboard!",
                    fontSize = 20.sp
                    )
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Display photos
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                itemsIndexed(images) { index, (bitmap, path) ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Photo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Delete button
                        IconButton(
                            onClick = {
                                val file = File(path)
                                if (file.exists()) file.delete()

                                scope.launch(Dispatchers.IO) {
                                    val task = database.taskDao().getTaskById(taskID)
                                    val updatedTask =
                                        task.copy(pictures = task.pictures.filter { it != path })
                                    database.taskDao().updateTask(updatedTask)
                                }

                                val updatedList = images.toMutableList()
                                updatedList.removeAt(index)
                                images = updatedList
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove photo"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = onBack) {Text("Back") }
                Spacer(modifier = Modifier.width(20.dp))
                Button(onClick = {
                    cameraLauncher.launch()
                }) {
                    Text("Take Photo")
                }
            }
        }
    }
}

fun savePhoto(context: Context, photo: Bitmap): String{

    val fileName = "task_photo_${UUID.randomUUID()}.png"
    val file = File(context.filesDir, fileName)

    val stream = FileOutputStream(file)
    photo.compress(Bitmap.CompressFormat.PNG, 100, stream)

    stream.flush()
    stream.close()

    return file.absolutePath
}