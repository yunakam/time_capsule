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
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.timecapsule.data.Note
import com.example.timecapsule.data.NoteDao
import com.google.accompanist.flowlayout.FlowRow


@Composable
fun NoteDialog(
    title: String = "",
    initialNote: Note = Note(),
    onSave: (Note) -> Unit,
    onDismiss: () -> Unit,
    noteDao: NoteDao,
    onCancel: (() -> Unit)? = null,
) {
    var text by remember { mutableStateOf(initialNote.text) }
    var saidWho by remember { mutableStateOf(initialNote.saidWho ?: "") }
    var title by remember { mutableStateOf(initialNote.title ?: "") }
    var url by remember { mutableStateOf(initialNote.url ?: "") }
    var page by remember { mutableStateOf(initialNote.page ?: "") }
    var source by remember { mutableStateOf(initialNote.source ?: "") }
    var confirmedTags by remember { mutableStateOf(initialNote.tags ?: emptyList()) }
    var tagInput by remember { mutableStateOf("") }

    // Error state for validation
    var showTitleMissingError by remember { mutableStateOf(false) }
    // FocusRequester for the title field
    val titleFocusRequester = remember { FocusRequester() }

    // For Edit: reset fields when note changes
    LaunchedEffect(initialNote) {
        text = initialNote.text
        saidWho = initialNote.saidWho ?: ""
        title = initialNote.title ?: ""
        url = initialNote.url ?: ""
        page = initialNote.page ?: ""
        source = initialNote.source ?: ""
        confirmedTags = initialNote.tags ?: emptyList()
        tagInput = ""
        showTitleMissingError = false
    }

    // Request focus on the Title field when showError becomes true
    LaunchedEffect(showTitleMissingError) {
        if (showTitleMissingError) {
            titleFocusRequester.requestFocus()
        }
    }

    val saidWhoSuggestions by rememberSuggestions(saidWho) { noteDao.getSaidWhoSuggestions(it) }
    val titleSuggestions by rememberSuggestions(title) { noteDao.getTitleSuggestions(it) }
    val sourceSuggestions by rememberSuggestions(source) { noteDao.getPublisherSuggestions(it) }

    // --- Tag Chip Logic ---
    val allTagsRaw by produceState(initialValue = emptyList<String?>()) {
        value = noteDao.getAllTagsRaw()
    }
    val allTags = allTagsRaw
        .asSequence() // Convert to Sequence for lazy evaluation to ensure that intermediate results are not stored in memory
        .filterNotNull()
        .flatMap { it.split(",") }
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .distinct()
        .toList() // Convert back to a List at the end

    // Suggestions for tag input
    val tagSuggestions = if (tagInput.isNotBlank()) {
        allTags.filter { it.startsWith(tagInput, ignoreCase = true) && !confirmedTags.contains(it) }.take(10)
    } else emptyList()

    val fields = listOf(
        FieldSpec(saidWho, { saidWho = it }, "saidWho", suggestions = saidWhoSuggestions, onSuggestionClick = { saidWho = it }),
        FieldSpec(
            title,
            { title = it },
            "Title",
            modifier = Modifier.focusRequester(titleFocusRequester),
            suggestions = titleSuggestions,
            onSuggestionClick = { title = it }),
        FieldSpec(page, { page = it.filter { it.isDigit() } }, "Page", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)),
        FieldSpec(source, { source = it }, "Publisher", suggestions = sourceSuggestions, onSuggestionClick = { source = it }),
        FieldSpec(url, { url = it }, "URL", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)),
        FieldSpec(
            tagInput,
            { input ->
                // Handle ',' character to add tag
                if (input.endsWith(",")) {
                    val trimmed = input.trimEnd(',')
                    if (trimmed.isNotEmpty() && !confirmedTags.contains(trimmed)) {
                        confirmedTags = confirmedTags + trimmed
                    }
                    tagInput = ""
                } else {
                    tagInput = input
                }
            },
            "Tag",
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    val trimmed = tagInput.trim()
                    if (trimmed.isNotEmpty() && !confirmedTags.contains(trimmed)) {
                        confirmedTags = confirmedTags + trimmed
                    }
                    tagInput = ""
                }
            ),
            suggestions = tagSuggestions,
            onSuggestionClick = { suggestion ->
                if (!confirmedTags.contains(suggestion)) {
                    confirmedTags = confirmedTags + suggestion
                }
                tagInput = ""
            }
        ),
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
                    Spacer(modifier = Modifier.height(18.dp))
                    fields.forEachIndexed { idx, (value, onChange, label, modifier, keyboardOptions, keyboardActions, suggestions, onSuggestionClick) ->
                        OptionalTextField(
                            value = value,
                            onValueChange = onChange,
                            label = label,
                            modifier = modifier,
                            keyboardOptions = keyboardOptions,
                            keyboardActions = keyboardActions,
                            suggestions = suggestions,
                            onSuggestionClick = onSuggestionClick
                        )

                        // Check if the current field is "Title" and show the error message if validation fails
                        if (label == "Title" && showTitleMissingError) {
                            Text(
                                text = "Required when Page or Publisher is filled.",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 32.dp, top = 4.dp)
                            )
                        }

                        if (idx < fields.size - 1) {
                            Spacer(modifier = Modifier.height(0.dp))
                        }
                    }

                    // Tag chips
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        mainAxisSpacing = 8.dp,
                        crossAxisSpacing = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 72.dp)
                    ) {
                        confirmedTags.forEach { tag ->
                            TagChip(
                                tag = tag,
                                onRemove = {
                                    confirmedTags = confirmedTags.filter { it != tag }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onCancel ?: onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            // Validation logic
                            val isSourceTitleMandatory = page.isNotBlank() || source.isNotBlank()
                            if (isSourceTitleMandatory && title.isBlank()) {
                                showTitleMissingError = true
                            } else {
                                showTitleMissingError = false
                                val newNote = initialNote.copy(
                                    text = text,
                                    saidWho = saidWho.ifBlank { null },
                                    title = title.ifBlank { null },
                                    url = url.ifBlank { null },
                                    page = page.ifBlank { null },
                                    source = source.ifBlank { null },
                                    tags = confirmedTags.takeIf { it.isNotEmpty() }
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
