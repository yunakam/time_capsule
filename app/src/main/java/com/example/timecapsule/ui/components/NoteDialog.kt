package com.example.timecapsule.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.timecapsule.data.Note
import com.example.timecapsule.data.NoteDao
import kotlin.random.Random


@Composable
fun NoteDialog(
//    title: String = "",
    initialNote: Note = Note(),
    onSave: (Note) -> Unit,
    onDismiss: () -> Unit,
    noteDao: NoteDao,
    onCancel: (() -> Unit)? = null,
) {
    var category by remember { mutableStateOf("BOOK") }
    val categories = listOf("BOOK", "WEB", "TALK", "THOUGHTS")

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
    val sourceSuggestions by rememberSuggestions(source) { noteDao.getSourceSuggestions(it) }

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

                    // Random quote placeholder for the text field
                    val randomPlaceholder = remember { TextFieldPlaceholders.quotes[Random.nextInt(TextFieldPlaceholders.quotes.size)] }

                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = { 
                            Text(
                                text = randomPlaceholder,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .heightIn(min = 180.dp),
                        singleLine = false,
                        maxLines = 10,
                    )

                    Spacer(Modifier.height(12.dp))

                    // Category selection
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        categories.forEach { cat ->
                            val isSelected = category == cat
                            OutlinedButton(
                                onClick = { category = cat },
                                enabled = true,
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .widthIn(min = 56.dp),
                                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                    contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    disabledContainerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                    disabledContentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                ),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                            ) { 
                                Text(
                                    text = cat,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                                )
                            }
                        }
                    }

                    val tagField = FieldSpec(
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
                    )

                    val fields = when (category) {

                        "BOOK" -> listOf(
                            FieldSpec(saidWho, { saidWho = it }, "saidWho", suggestions = saidWhoSuggestions, onSuggestionClick = { saidWho = it }),
                            FieldSpec(title, { title = it }, "Title", suggestions = titleSuggestions, onSuggestionClick = { title = it }),
                            FieldSpec(source, { source = it }, "Publisher", suggestions = sourceSuggestions, onSuggestionClick = { source = it }),
                            FieldSpec(page, { page = it }, "Page")
                        ) + tagField
                        "WEB" -> listOf(
                            FieldSpec(saidWho, { saidWho = it }, "saidWho", suggestions = saidWhoSuggestions, onSuggestionClick = { saidWho = it }),
                            FieldSpec(title, { title = it }, "Title", suggestions = titleSuggestions, onSuggestionClick = { title = it }),
                            FieldSpec(source, { source = it }, "Website", suggestions = sourceSuggestions, onSuggestionClick = { source = it }),
                            FieldSpec(url, { url = it }, "URL")
                        ) + tagField
                        "TALK" -> listOf(
                            FieldSpec(saidWho, { saidWho = it }, "saidWho", suggestions = saidWhoSuggestions, onSuggestionClick = { saidWho = it }),
                            FieldSpec(title, { title = it }, "Title", suggestions = titleSuggestions, onSuggestionClick = { title = it }),
                            FieldSpec(source, { source = it }, "Channel")
                        ) + tagField
                        "THOUGHTS" -> listOf(
                            FieldSpec(source, { source = it }, "Where?", maxLines = 3, singleLine = false)
                        ) + tagField
                        else -> emptyList()
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    fields.forEachIndexed { idx, (value, onChange, label, modifier, maxLines, singleLine, keyboardOptions, keyboardActions, suggestions, onSuggestionClick) ->
                        OptionalTextField(
                            value = value,
                            onValueChange = onChange,
                            label = label,
                            modifier = modifier,
                            maxLines = maxLines,
                            singleLine = singleLine,
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
                            Spacer(modifier = Modifier.height(4.dp)) // Reduce spacing between fields
                        }
                    }

                    // Tag chips
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
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

                    Spacer(modifier = Modifier.height(8.dp))
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
                            // Validation logic
//                            val isSourceTitleMandatory = page.isNotBlank() || source.isNotBlank()
//                            if (isSourceTitleMandatory && title.isBlank()) {
//                                showTitleMissingError = true
//                            } else {
//                                showTitleMissingError = false
//                                val newNote = initialNote.copy(
//                                    text = text,
//                                    saidWho = saidWho.ifBlank { null },
//                                    title = title.ifBlank { null },
//                                    url = url.ifBlank { null },
//                                    page = page.ifBlank { null },
//                                    source = source.ifBlank { null },
//                                    tags = confirmedTags.takeIf { it.isNotEmpty() }
//                                )
//                                onSave(newNote)
//                                onDismiss()
//                            }
                        },
                        enabled = text.isNotBlank()
                    ) { Text("Save") }
                }
            }
        }
    }
}
