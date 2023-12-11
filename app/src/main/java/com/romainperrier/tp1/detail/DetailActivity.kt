package com.romainperrier.tp1.detail

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.romainperrier.tp1.detail.ui.theme.RomainPerrierPASTP1Theme
import com.romainperrier.tp1.list.Task
import java.util.UUID

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RomainPerrierPASTP1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    var task = (intent.getSerializableExtra("task") as Task?)?: Task(UUID.randomUUID().toString())

                    Detail(initialTask= task, onValidate = { task ->
                        val intent = Intent().apply {
                            putExtra("task", task)
                        }
                        setResult(RESULT_OK, intent)
                        finish()
                    })
                }
            }
        }
    }

    @Composable
    fun Detail(modifier: Modifier = Modifier, initialTask: Task, onValidate: (Task) -> Unit) {
        var task by remember { mutableStateOf(initialTask) }

        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Task Detail",
                style = MaterialTheme.typography.headlineLarge
            )
            OutlinedTextField(
                value = task.title,
                onValueChange = { task = task.copy(title = it) },
                label = { Text("Title") },
                textStyle = MaterialTheme.typography.headlineLarge
            )
            OutlinedTextField(
                value = task.description,
                onValueChange = { task = task.copy(description = it) },
                label = { Text("Description") },
                textStyle = MaterialTheme.typography.headlineLarge
            )
            Button(onClick = {
                onValidate(task)
            }) {
                Text("Validate")
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DetailPreview() {
        RomainPerrierPASTP1Theme {
            Detail(initialTask = Task(UUID.randomUUID().toString()), onValidate = {})
        }
    }
}