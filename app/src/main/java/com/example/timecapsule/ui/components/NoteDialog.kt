package com.example.timecapsule.ui.components

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
import com.example.timecapsule.data.Note
import com.example.timecapsule.data.NoteDao

@Composable
fun NoteDialog(
    title: String,
    initialNote: Note = Note(), // Use a default empty note for Add
    onSave: (Note) -> Unit,
    onDismiss: () -> Unit,
    noteDao: NoteDao,
    cancelButtonAction: (() -> Unit)? = null, // For Edit: onCancelToView, for Add: onDismiss
) {
    var text by remember { mutableStateOf(initialNote.text) }
    var author by remember { mutableStateOf(initialNote.author ?: "") }
    var sourceTitle by remember { mutableStateOf(initialNote.sourceTitle ?: "") }
    var sourceUrl by remember { mutableStateOf(initialNote.sourceUrl ?: "") }
    var page by remember { mutableStateOf(initialNote.page ?: "") }
    var publisher by remember { mutableStateOf(initialNote.publisher ?: "") }
    var tags by remember { mutableStateOf(initialNote.tags ?: "") }

    // For EditNoteDialog: reset fields when note changes
    LaunchedEffect(initialNote) {
        text = initialNote.text
        author = initialNote.author ?: ""
        sourceTitle = initialNote.sourceTitle ?: ""
        sourceUrl = initialNote.sourceUrl ?: ""
        page = initialNote.page ?: ""
        publisher = initialNote.publisher ?: ""
        tags = initialNote.tags ?: ""
    }

    val authorSuggestions by rememberSuggestions(author) { noteDao.getAuthorSuggestions(it) }
    val titleSuggestions by rememberSuggestions(sourceTitle) { noteDao.getTitleSuggestions(it) }
    val publisherSuggestions by rememberSuggestions(publisher) { noteDao.getPublisherSuggestions(it) }
    val tagSuggestions by rememberSuggestions(tags) { noteDao.getTagSuggestions(it) }

    val fields = listOf(
        FieldSpec(author, { author = it }, "Author", suggestions = authorSuggestions, onSuggestionClick = { author = it }),
        FieldSpec(sourceTitle, { sourceTitle = it }, "Title", suggestions = titleSuggestions, onSuggestionClick = { sourceTitle = it }),
        FieldSpec(sourceUrl, { sourceUrl = it }, "URL", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)),
        FieldSpec(page, { page = it.filter { it.isDigit() } }, "Page", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)),
        FieldSpec(publisher, { publisher = it }, "Publisher", suggestions = publisherSuggestions, onSuggestionClick = { publisher = it }),
        FieldSpec(tags, { tags = it }, "Tags (comma separated, optional)", suggestions = tagSuggestions, onSuggestionClick = { tags = it })
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
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text(title) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 180.dp),
                        singleLine = false,
                        maxLines = 10,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    fields.forEachIndexed { idx, (value, onChange, label, keyboardOptions, suggestions, onSuggestionClick) ->
                        OptionalTextField(
                            value = value,
                            onValueChange = onChange,
                            label = label,
                            modifier = Modifier,
                            keyboardOptions = keyboardOptions,
                            suggestions = suggestions,
                            onSuggestionClick = onSuggestionClick
                        )
                        if (idx < fields.size - 1) {
                            Spacer(modifier = Modifier.height(0.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = cancelButtonAction ?: onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (text.isNotBlank()) {
                                val newNote = initialNote.copy(
                                    text = text,
                                    author = author.ifBlank { null },
                                    sourceTitle = sourceTitle.ifBlank { null },
                                    sourceUrl = sourceUrl.ifBlank { null },
                                    page = page.ifBlank { null },
                                    publisher = publisher.ifBlank { null },
                                    tags = tags.ifBlank { null }
                                )
                                onSave(newNote)
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
