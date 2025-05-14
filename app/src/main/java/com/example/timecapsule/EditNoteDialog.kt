package com.example.timecapsule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun EditNoteDialog(
    note: Note,
    onSave: (Note) -> Unit,
//    onDelete: (Note) -> Unit,
    onDismiss: () -> Unit,
    onCancelToView: () -> Unit
) {
    var text by remember { mutableStateOf(note.text) }
    var author by remember { mutableStateOf(note.author ?: "") }
    var sourceTitle by remember { mutableStateOf(note.sourceTitle ?: "") }
    var sourceUrl by remember { mutableStateOf(note.sourceUrl ?: "") }
    var page by remember { mutableStateOf(note.page ?: "") }
    var publisher by remember { mutableStateOf(note.publisher ?: "") }
    var tags by remember { mutableStateOf(note.tags ?: "") }

    // Reset fields if a different note is loaded
    LaunchedEffect(note) {
        text = note.text
        author = note.author ?: ""
        sourceTitle = note.sourceTitle ?: ""
        sourceUrl = note.sourceUrl ?: ""
        page = note.page ?: ""
        publisher = note.publisher ?: ""
        tags = note.tags ?: ""
    }

    val fields = listOf(
        FieldSpec(author, { v -> author = v }, "Author"),
        FieldSpec(sourceTitle, { v -> sourceTitle = v }, "Title"),
        FieldSpec(sourceUrl,
            { v -> sourceUrl = v },
            "URL",
            KeyboardOptions(keyboardType = KeyboardType.Uri)
        ),
        FieldSpec(
            page,
            { v -> page = v.filter { it.isDigit() } },
            "Page",
            KeyboardOptions(keyboardType = KeyboardType.Number)
        ),
        FieldSpec(publisher, { v -> publisher = v }, "Publisher"),
        FieldSpec(tags, { v -> tags = v }, "Tags (comma separated, optional)")
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .heightIn(max = 600.dp)
                    .padding(24.dp)
            ) {
                // Scrollable text fields
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Edit note") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 180.dp),
                        singleLine = false,
                        maxLines = 10,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    fields.forEachIndexed { idx, (value, onChange, label) ->
                        CompactBorderlessTextField(
                            value = value,
                            onValueChange = onChange,
                            label = label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp)
                        )
                        if (idx < fields.size - 1) {
                            Spacer(modifier = Modifier.height(0.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Fixed action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onCancelToView) { Text("Cancel") }
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
                    ) { Text("Save") }
                }
            }
        }
    }
}
