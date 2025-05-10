package com.example.timecapsule

import CompactBorderlessTextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp

@Composable
fun EditNoteDialog(
    note: Note,
    onSave: (Note) -> Unit,
    onDelete: (Note) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(note.text) }
    var author by remember { mutableStateOf(note.author ?: "") }
    var sourceTitle by remember { mutableStateOf(note.sourceTitle ?: "") }
    var sourceUrl by remember { mutableStateOf(note.sourceUrl ?: "") }
    var page by remember { mutableStateOf(note.page ?: "") }
    var publisher by remember { mutableStateOf(note.publisher ?: "") }
    var tags by remember { mutableStateOf(note.tags ?: "") }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val fields = listOf(
        Triple(author, { v: String -> author = v }, "Author (optional)"),
        Triple(sourceTitle, { v: String -> sourceTitle = v }, "Source Title (optional)"),
        Triple(sourceUrl, { v: String -> sourceUrl = v }, "Source URL (optional)"),
        Triple(page, { v: String -> page = v }, "Page (optional)"),
        Triple(publisher, { v: String -> publisher = v }, "Publisher (optional)"),
        Triple(tags, { v: String -> tags = v }, "Tags (comma separated, optional)")
    )

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(note)
                    showDeleteConfirm = false
                    onDismiss()
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Note") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
            ) {
                // Main note field
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Note") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    singleLine = false,
                    maxLines = 10,
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
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (text.isNotBlank()) {
                            onSave(
                                note.copy(
                                    text = text,
                                    author = author.ifBlank { null },
                                    sourceTitle = sourceTitle.ifBlank { null },
                                    sourceUrl = sourceUrl.ifBlank { null },
                                    page = page.ifBlank { null },
                                    publisher = publisher.ifBlank { null },
                                    tags = tags.ifBlank { null }
                                )
                            )
                            onDismiss()
                        }
                    },
                    enabled = text.isNotBlank()
                ) {
                    Text("Save")
                }
            }
        },
//        dismissButton = {}
    )
}
