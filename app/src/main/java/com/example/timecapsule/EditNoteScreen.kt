package com.example.timecapsule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    note: Note,
    onSave: (Note) -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit,
    showDeleteDialog: Boolean,
    onConfirmDelete: () -> Unit,
    onDismissDeleteDialog: () -> Unit
) {
    var noteText by remember { mutableStateOf(note.text) }
    var author by remember { mutableStateOf(note.author ?: "") }
    var sourceTitle by remember { mutableStateOf(note.sourceTitle ?: "") }
    var sourceUrl by remember { mutableStateOf(note.sourceUrl ?: "") }
    var page by remember { mutableStateOf(note.page ?: "") }
    var publisher by remember { mutableStateOf(note.publisher ?: "") }
    var tags by remember { mutableStateOf(note.tags ?: "") }
    var isSaving by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

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
                title = { Text("Edit Note") },
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete")
                }
                Button(
                    onClick = {
                        isSaving = true
                        onSave(
                            note.copy(
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
                                    .takeIf { it.isNotBlank() }
                            )
                        )
                        isSaving = false
                    },
                    enabled = noteText.isNotBlank() && !isSaving,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
            }
        }

        // Delete confirmation dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = onDismissDeleteDialog,
                title = { Text("Delete Note") },
                text = { Text("Are you sure you want to delete this note?") },
                confirmButton = {
                    TextButton(onClick = onConfirmDelete) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDeleteDialog) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
