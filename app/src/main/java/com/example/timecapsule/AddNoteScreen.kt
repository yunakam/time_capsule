package com.example.timecapsule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun CompactBorderlessTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 24.dp),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodySmall,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    db: AppDatabase,
    onClose: () -> Unit,
    onNoteAdded: () -> Unit
) {
    var noteText by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var sourceTitle by remember { mutableStateOf("") }
    var sourceUrl by remember { mutableStateOf("") }
    var page by remember { mutableStateOf("") }
    var publisher by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // List of triples: value, onValueChange, label
    val fields = listOf(
        Triple(author, { v: String -> author = v }, "Author (optional)"),
        Triple(sourceTitle, { v: String -> sourceTitle = v }, "Source Title (optional)"),
        Triple(sourceUrl, { v: String -> sourceUrl = v }, "Source URL (optional)"),
        Triple(page, { v: String -> page = v }, "Page (optional)"),
        Triple(publisher, { v: String -> publisher = v }, "Publisher (optional)"),
        Triple(tags, { v: String -> tags = v }, "Tags (comma separated, optional)")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Note") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Main note field
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Note") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                singleLine = false,
                maxLines = 10
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Loop through all compact fields
            fields.forEachIndexed { idx, (value, onChange, label) ->
                CompactBorderlessTextField(
                    value = value,
                    onValueChange = onChange,
                    label = label,
                    modifier = Modifier.fillMaxWidth()
                )
                if (idx < fields.size - 1) {
                    Spacer(modifier = Modifier.height(0.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    isSaving = true
                    scope.launch(Dispatchers.IO) {
                        db.noteDao().insert(
                            Note(
                                text = noteText,
                                author = author.takeIf { it.isNotBlank() },
                                sourceTitle = sourceTitle.takeIf { it.isNotBlank() },
                                sourceUrl = sourceUrl.takeIf { it.isNotBlank() },
                                page = page.takeIf { it.isNotBlank() },
                                publisher = publisher.takeIf { it.isNotBlank() },
                                tags = tags.split(',')
                                    .map { it.trim() }
                                    .filter { it.isNotEmpty() }
                                    .joinToString(",")
                                    .takeIf { it.isNotBlank() },
                                date = Date()
                            )
                        )
                        isSaving = false
                        onNoteAdded()
                    }
                },
                enabled = noteText.isNotBlank() && !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}
