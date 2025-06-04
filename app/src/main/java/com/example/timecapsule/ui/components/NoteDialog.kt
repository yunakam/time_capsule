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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.timecapsule.data.Note
import com.example.timecapsule.data.NoteCategory
import com.example.timecapsule.data.NoteDao
import com.example.timecapsule.data.SourceBindingDao
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun NoteDialog(
    initialNote: Note = Note(),
    onSave: (Note) -> Unit,
    onDismiss: () -> Unit,
    noteDao: NoteDao,
    sourceBindingDao: SourceBindingDao,
    onCancel: (() -> Unit)? = null,
) {
    val coroutineScope = rememberCoroutineScope()
    var category by remember { mutableStateOf(initialNote.category?.name ?: "BOOK") }
    val categories = NoteCategory.values().map { it.name }

    var text by remember { mutableStateOf(initialNote.text) }
    var saidWho by remember { mutableStateOf(initialNote.saidWho ?: "") }
    var title by remember { mutableStateOf(initialNote.title ?: "") }
    var url by remember { mutableStateOf(initialNote.url ?: "") }
    var page by remember { mutableStateOf(initialNote.page ?: "") }
    var source by remember { mutableStateOf(initialNote.source ?: "") }
    var confirmedTags by remember { mutableStateOf(initialNote.tags ?: emptyList()) }
    var tagInput by remember { mutableStateOf("") }

    // Track which field was last selected from suggestions
    var lastSelectedField by remember { mutableStateOf<String?>(null) }
    var lastSelectedValue by remember { mutableStateOf<String?>(null) }

    // Error state for validation
    var showTitleMissingError by remember { mutableStateOf(false) }
    // FocusRequester for the title field
    val titleFocusRequester = remember { FocusRequester() }

    // For Edit: reset fields when note changes
    LaunchedEffect(initialNote) {
        category = initialNote.category?.name ?: "BOOK"
        text = initialNote.text
        saidWho = initialNote.saidWho ?: ""
        title = initialNote.title ?: ""
        url = initialNote.url ?: ""
        page = initialNote.page ?: ""
        source = initialNote.source ?: ""
        confirmedTags = initialNote.tags ?: emptyList()
        tagInput = ""
        showTitleMissingError = false
        lastSelectedField = null
        lastSelectedValue = null
    }

    // Request focus on the Title field when showError becomes true
    LaunchedEffect(showTitleMissingError) {
        if (showTitleMissingError) {
            titleFocusRequester.requestFocus()
        }
    }

    // Dynamic suggestions based on last selected field
    val saidWhoSuggestions by produceState(initialValue = emptyList<String>(), saidWho, lastSelectedField, lastSelectedValue) {
        value = when {
            saidWho.isNotBlank() -> // First check if there's direct input
                noteDao.getSaidWhoSuggestions(saidWho)
            lastSelectedField == "title" && lastSelectedValue != null -> 
                sourceBindingDao.getSaidWhosForTitle(lastSelectedValue!!)
            lastSelectedField == "source" && lastSelectedValue != null ->
                sourceBindingDao.findByTitle(title)
                    .map { it.saidWho }
                    .distinct()
            else -> emptyList()
        }
    }

    val titleSuggestions by produceState(initialValue = emptyList<String>(), title, lastSelectedField, lastSelectedValue) {
        value = when {
            title.isNotBlank() -> // First check if there's direct input
                noteDao.getTitleSuggestions(title)
            lastSelectedField == "saidWho" && lastSelectedValue != null ->
                sourceBindingDao.getTitlesForSaidWho(lastSelectedValue!!)
            lastSelectedField == "source" && lastSelectedValue != null ->
                sourceBindingDao.findByTitle(title)
                    .map { it.title }
                    .distinct()
            else -> emptyList()
        }
    }

    val sourceSuggestions by produceState(initialValue = emptyList<String>(), source, lastSelectedField, lastSelectedValue) {
        value = when {
            source.isNotBlank() -> // First check if there's direct input
                noteDao.getSourceSuggestions(source)
            lastSelectedField == "saidWho" && lastSelectedValue != null ->
                sourceBindingDao.findBySaidWho(lastSelectedValue!!)
                    .mapNotNull { it.source }
                    .distinct()
            lastSelectedField == "title" && lastSelectedValue != null ->
                sourceBindingDao.getSourcesForTitle(lastSelectedValue!!)
                    .filterNotNull()
            else -> emptyList()
        }
    }

    // --- Pending Suggestions State ---
    var pendingTitleSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var pendingSourceSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var pendingSaidWhoSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var titleFieldFocused by remember { mutableStateOf(false) }
    var sourceFieldFocused by remember { mutableStateOf(false) }
    var saidWhoFieldFocused by remember { mutableStateOf(false) }

    // --- Helper: Filter suggestions by input ---
    fun filterSuggestions(input: String, suggestions: List<String>): List<String> {
        return suggestions.filter { it.startsWith(input, ignoreCase = true) }
    }

    // --- Modified handleSelection logic ---
    suspend fun handleSelection(field: String, value: String) {
        lastSelectedField = field
        lastSelectedValue = value

        when (field) {
            "saidWho" -> {
                saidWho = value
                val notes = noteDao.getNotesByAuthor(value)
                val uniqueTitles = notes.mapNotNull { it.title }.distinct()
                val uniqueSources = notes.mapNotNull { it.source }.distinct()

                if (title.isBlank()) {
                    if (uniqueTitles.size == 1) {
                        title = uniqueTitles.first()
                        pendingTitleSuggestions = emptyList()
                    } else if (uniqueTitles.size > 1) {
                        pendingTitleSuggestions = uniqueTitles
                    } else {
                        pendingTitleSuggestions = emptyList()
                    }
                }
                if (source.isBlank()) {
                    if (uniqueSources.size == 1) {
                        source = uniqueSources.first()
                        pendingSourceSuggestions = emptyList()
                    } else if (uniqueSources.size > 1) {
                        pendingSourceSuggestions = uniqueSources
                    } else {
                        pendingSourceSuggestions = emptyList()
                    }
                }
            }
            "title" -> {
                title = value
                val notes = noteDao.getNotesByTitle(value)
                val uniqueSaidWhos = notes.mapNotNull { it.saidWho }.distinct()
                val uniqueSources = notes.mapNotNull { it.source }.distinct()
                
                if (saidWho.isBlank()) {
                    if (uniqueSaidWhos.size == 1) {
                        saidWho = uniqueSaidWhos.first()
                        pendingSaidWhoSuggestions = emptyList()
                    } else if (uniqueSaidWhos.size > 1) {
                        pendingSaidWhoSuggestions = uniqueSaidWhos
                    } else {
                        pendingSaidWhoSuggestions = emptyList()
                    }
                }
                if (source.isBlank()) {
                    if (uniqueSources.size == 1) {
                        source = uniqueSources.first()
                        pendingSourceSuggestions = emptyList()
                    } else if (uniqueSources.size > 1) {
                        pendingSourceSuggestions = uniqueSources
                    } else {
                        pendingSourceSuggestions = emptyList()
                    }
                }
            }
            "source" -> {
                source = value
                val notes = noteDao.getNotesByPublisher(value)
                val uniqueSaidWhos = notes.mapNotNull { it.saidWho }.distinct()
                val uniqueTitles = notes.mapNotNull { it.title }.distinct()

                if (saidWho.isBlank()) {
                    if (uniqueSaidWhos.size == 1) {
                        saidWho = uniqueSaidWhos.first()
                        pendingSaidWhoSuggestions = emptyList()
                    } else if (uniqueSaidWhos.size > 1) {
                        pendingSaidWhoSuggestions = uniqueSaidWhos
                    } else {
                        pendingSaidWhoSuggestions = emptyList()
                    }
                }
                if (title.isBlank()) {
                    if (uniqueTitles.size == 1) {
                        title = uniqueTitles.first()
                        pendingTitleSuggestions = emptyList()
                    } else if (uniqueTitles.size > 1) {
                        pendingTitleSuggestions = uniqueTitles
                    } else {
                        pendingTitleSuggestions = emptyList()
                    }
                }
            }
        }
    }

    // --- Field Focus Handlers ---
    fun onSaidWhoFocus(focused: Boolean) { saidWhoFieldFocused = focused }
    fun onTitleFocus(focused: Boolean) { titleFieldFocused = focused }
    fun onSourceFocus(focused: Boolean) { sourceFieldFocused = focused }

    // --- Suggestion Providers ---
    val saidWhoSuggestionList = if (saidWhoFieldFocused && pendingSaidWhoSuggestions.isNotEmpty()) {
        if (saidWho.isBlank()) {
            pendingSaidWhoSuggestions
        } else {
            filterSuggestions(saidWho, pendingSaidWhoSuggestions)
        }
    } else {
        saidWhoSuggestions
    }
    val titleSuggestionList = if (titleFieldFocused && pendingTitleSuggestions.isNotEmpty()) {
        if (title.isBlank()) pendingTitleSuggestions else filterSuggestions(title, pendingTitleSuggestions)
    } else {
        titleSuggestions
    }
    val sourceSuggestionList = if (sourceFieldFocused && pendingSourceSuggestions.isNotEmpty()) {
        if (source.isBlank()) pendingSourceSuggestions else filterSuggestions(source, pendingSourceSuggestions)
    } else {
        sourceSuggestions
    }

    // --- Clear pending suggestions on selection ---
    fun clearPendingSuggestions(field: String) {
        when (field) {
            "saidWho" -> pendingSaidWhoSuggestions = emptyList()
            "title" -> pendingTitleSuggestions = emptyList()
            "source" -> pendingSourceSuggestions = emptyList()
        }
    }

    // --- Modified onXSelected to clear pending suggestions ---
    fun onSaidWhoSelected(selectedSaidWho: String) {
        coroutineScope.launch {
            handleSelection("saidWho", selectedSaidWho)
            clearPendingSuggestions("saidWho")
        }
    }
    fun onTitleSelected(selectedTitle: String) {
        coroutineScope.launch {
            handleSelection("title", selectedTitle)
            clearPendingSuggestions("title")
        }
    }
    fun onSourceSelected(selectedSource: String) {
        coroutineScope.launch {
            handleSelection("source", selectedSource)
            clearPendingSuggestions("source")
        }
    }

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

                    // Main text input field
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
                        maxLines = 13,
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
                            FieldSpec(saidWho, { saidWho = it }, "saidWho", suggestions = saidWhoSuggestionList, onSuggestionClick = { onSaidWhoSelected(it) }, onFocusChanged = { onSaidWhoFocus(it) }),
                            FieldSpec(title, { title = it }, "Title", suggestions = titleSuggestionList, onSuggestionClick = { onTitleSelected(it) }, onFocusChanged = { onTitleFocus(it) }),
                            FieldSpec(source, { source = it }, "Publisher", suggestions = sourceSuggestionList, onSuggestionClick = { onSourceSelected(it) }, onFocusChanged = { onSourceFocus(it) }),
                            FieldSpec(page, { page = it }, "Page", keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)),
                        ) + tagField
                        "WEB" -> listOf(
                            FieldSpec(saidWho, { saidWho = it }, "saidWho", suggestions = saidWhoSuggestionList, onSuggestionClick = { onSaidWhoSelected(it) }, onFocusChanged = { onSaidWhoFocus(it) }),
                            FieldSpec(title, { title = it }, "Title", suggestions = titleSuggestionList, onSuggestionClick = { onTitleSelected(it) }, onFocusChanged = { onTitleFocus(it) }),
                            FieldSpec(source, { source = it }, "Website", suggestions = sourceSuggestionList, onSuggestionClick = { onSourceSelected(it) }, onFocusChanged = { onSourceFocus(it) }),
                            FieldSpec(url, { url = it }, "URL")
                        ) + tagField
                        "TALK" -> listOf(
                            FieldSpec(saidWho, { saidWho = it }, "saidWho", suggestions = saidWhoSuggestionList, onSuggestionClick = { onSaidWhoSelected(it) }, onFocusChanged = { onSaidWhoFocus(it) }),
                            FieldSpec(title, { title = it }, "Title", suggestions = titleSuggestionList, onSuggestionClick = { onTitleSelected(it) }, onFocusChanged = { onTitleFocus(it) }),
                            FieldSpec(source, { source = it }, "Channel", suggestions = sourceSuggestionList, onSuggestionClick = { onSourceSelected(it) }, onFocusChanged = { onSourceFocus(it) }),
                            FieldSpec(page, { page = it }, "Number", keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)),
                            FieldSpec(url, { url = it }, "URL")
                        ) + tagField
                        "THOUGHTS" -> listOf(
                            FieldSpec(source, { source = it }, "Where?", maxLines = 3, singleLine = false, suggestions = sourceSuggestionList, onSuggestionClick = { onSourceSelected(it) }, onFocusChanged = { onSourceFocus(it) })
                        ) + tagField
                        else -> emptyList()
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    fields.forEachIndexed { idx, (value, onChange, label, modifier, maxLines, singleLine, keyboardOptions, keyboardActions, suggestions, onSuggestionClick, onFocusChanged) ->
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
                            onSuggestionClick = onSuggestionClick,
                            onFocusChanged = onFocusChanged
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
                                category = NoteCategory.valueOf(category),
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
                        },
                        enabled = text.isNotBlank()
                    ) { Text("Save") }
                }
            }
        }
    }
}
