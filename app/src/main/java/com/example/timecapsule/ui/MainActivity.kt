package com.example.timecapsule.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.example.compose.ThemeType
import com.example.timecapsule.R
import com.example.timecapsule.data.AppDatabase
import com.example.timecapsule.data.FilterType
import com.example.timecapsule.data.Note
import com.example.timecapsule.data.NoteRepository
import com.example.timecapsule.data.NoteScore
import com.example.timecapsule.data.PreferencesManager
import com.example.timecapsule.ui.components.DeleteNoteDialog
import com.example.timecapsule.ui.components.NoteCard
import com.example.timecapsule.ui.components.NoteDialog
import com.example.timecapsule.ui.components.NoteViewDialog
import com.example.timecapsule.ui.components.OnLaunch
import com.example.timecapsule.ui.components.SettingsDialog
import com.example.timecapsule.ui.components.SortType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(this)
        val preferencesManager = PreferencesManager(this)

        setContent {
            val themeType by preferencesManager.themeTypeFlow.collectAsState(initial = ThemeType.Default)
            AppTheme(dynamicColor = false, themeType = themeType) {
                var showFilteredNotesScreen by remember { mutableStateOf(false) }
                var currentFilterType by remember { mutableStateOf<FilterType?>(null) }
                var currentFilterValue by remember { mutableStateOf<String?>(null) }

                var showAddDialog by remember { mutableStateOf(false) }
                var showViewDialogId by remember { mutableStateOf<Long?>(null) }
                var showEditDialogId by remember { mutableStateOf<Long?>(null) }
                var showDeleteDialogId by remember { mutableStateOf<Long?>(null) }

                var isSearchActive by remember { mutableStateOf(false) }
                var searchQuery by remember { mutableStateOf("") }

                var showSettingsDialog by remember { mutableStateOf(false) }

                // Sort type state
                var sortType by remember { mutableStateOf(SortType.Newest) }
                var showSortMenu by remember { mutableStateOf(false) }

                val scope = rememberCoroutineScope()
                val notesFlow = remember { db.noteDao().getAllFlow() }
                val notes by notesFlow.collectAsState(initial = emptyList())
                val noteRepository = remember {
                    NoteRepository(db.noteDao(), db.noteVisitDao())
                }

                // Update score for each note on app launch
                LaunchedEffect(notes) {
                    withContext(Dispatchers.IO) {
                        notes.forEach { note ->
                            // Construct NoteScore from note's properties
                            val noteScore = NoteScore(
                                score = note.score,
                                lastUpdated = note.lastUpdated,
                                visitTimestamps = note.visitTimestamps?.toMutableList() ?: mutableListOf()
                            )
                            val oldScore = note.score
                            noteScore.calculateScore() // This will update the score if needed
                            if (noteScore.calculateScore() != oldScore) {
                                // Update note in DB if score changed
                                db.noteDao().update(
                                    note.copy(
                                        score = noteScore.calculateScore(),
                                        lastUpdated = noteScore.lastUpdated,
                                        visitTimestamps = noteScore.visitTimestamps
                                    )
                                )
                            }
                        }
                    }
                }

                val topPageSetting by preferencesManager.topPageSettingFlow.collectAsState(initial = null)

                val noteDao = db.noteDao()
                val sourceBindingDao = db.sourceBindingDao()

                // Only proceed when preferences are loaded
                if (topPageSetting == null) {
                    // Show a loading indicator or splash
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    // --- Startup behavior: Show dialog based on preference, but only once ---
                    var didShowStartupDialog by remember { mutableStateOf(false) }
                    LaunchedEffect(topPageSetting, notes) {
                        if (!didShowStartupDialog) {
                            when (topPageSetting) {
                                OnLaunch.ADD_NOTE_DIALOG -> showAddDialog = true
                                OnLaunch.RANDOM_NOTE -> {
                                    if (notes.isNotEmpty()) {
                                        val randomNote = notes[Random.nextInt(notes.size)]
                                        withContext(Dispatchers.IO) {
                                            noteRepository.logNoteVisitAndScore(randomNote.id)
                                            val updatedNote = db.noteDao().getById(randomNote.id)
                                            Log.d(
                                                "NoteVisit",
                                                "Note id: ${randomNote.id} is visited. Visit count: ${updatedNote?.visitCount ?: "?"}"
                                            )
                                        }
                                        showViewDialogId = randomNote.id
                                    }
                                }
                                OnLaunch.LOWEST_SCORE_NOTE -> {
                                    if (notes.isNotEmpty()) {
                                        val minScore = notes.minOf { it.score }
                                        val lowestScoreNotes = notes.filter { it.score == minScore }
                                        val noteToShow = lowestScoreNotes.random()
                                        withContext(Dispatchers.IO) {
                                            noteRepository.logNoteVisitAndScore(noteToShow.id)
                                            val updatedNote = db.noteDao().getById(noteToShow.id)
                                            Log.d(
                                                "NoteVisit",
                                                "Note id: ${noteToShow.id} is visited. Visit count: ${updatedNote?.visitCount ?: "?"}"
                                            )
                                        }
                                        showViewDialogId = noteToShow.id
                                    }
                                }
                                OnLaunch.NOTE_LIST -> { /* Do nothing */ }
                                null -> { /* Should not happen here */ }
                            }
                            didShowStartupDialog = true
                        }
                    }

                    // Color logic
                    val sortedNotes = notes.sortedByDescending { it.createdAt }
                    val colorBuckets = listOf(
                        lerp(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.primary, 0.1f),
                        lerp(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.primary, 0.2f),
                        lerp(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.primary, 0.3f),
                        lerp(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.primary, 0.4f),
                    )
                    val colorForNote: (Note) -> Color = { note ->
                        val sortedByScore = notes.sortedBy { it.score }
                        val bucketSize = (notes.size / colorBuckets.size.toFloat()).coerceAtLeast(1f)
                        val scoreIndex = sortedByScore.indexOfFirst { it.id == note.id }
                        if (scoreIndex == -1) colorBuckets.last() else {
                            val bucket = ((colorBuckets.size - 1) - (scoreIndex / bucketSize).toInt()).coerceIn(0, colorBuckets.lastIndex)
                            colorBuckets[bucket]
                        }
                    }

                    if (showFilteredNotesScreen && currentFilterType != null && currentFilterValue != null) {
                        FilteredNotesScreen(
                            filterType = currentFilterType!!,
                            filterValue = currentFilterValue!!,
                            allNotes = notes,
                            onDismiss = {
                                showFilteredNotesScreen = false
                                currentFilterType = null
                                currentFilterValue = null
                            },
                            onNoteClick = { note ->
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        noteRepository.logNoteVisitAndScore(note.id)
                                    }
                                    showViewDialogId = note.id
                                }
                            },
                            onNoteDeleteClick = { note -> showDeleteDialogId = note.id },
                            colorForNote = colorForNote
                        )
                    } else {
                        Scaffold(
                            floatingActionButton = {
                                if (!isSearchActive) {
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        modifier = Modifier.padding(end = 16.dp, bottom = 24.dp)
                                    ) {
                                        FloatingActionButton(
                                            onClick = { showSettingsDialog = true },
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .padding(end = 12.dp, bottom = 16.dp)
                                                .size(48.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = "Settings",
                                                tint = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                        Row(
                                            verticalAlignment = Alignment.Bottom
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.CenterEnd,
                                            ) {
                                                FloatingActionButton(
                                                    onClick = { showSortMenu = true },
                                                    modifier = Modifier
                                                        .padding(end = 16.dp, bottom = 12.dp)
                                                        .width(200.dp)
                                                        .size(48.dp),
                                                    shape = CircleShape,
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.padding(horizontal = 12.dp)
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(id = R.drawable.ic_sort),
                                                            contentDescription = "Sort",
                                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            text = sortType.label,
                                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                            style = MaterialTheme.typography.labelLarge
                                                        )
                                                    }
                                                }
                                                DropdownMenu(
                                                    expanded = showSortMenu,
                                                    onDismissRequest = { showSortMenu = false },
                                                    modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer),
                                                    offset = androidx.compose.ui.unit.DpOffset(x = 40.dp, y = (-8).dp),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    SortType.menuOptions.forEach { option ->
                                                        DropdownMenuItem(
                                                            text = { 
                                                                Text(
                                                                    option.menuText,
                                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                                ) 
                                                            },
                                                            onClick = {
                                                                sortType = option
                                                                showSortMenu = false
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                            FloatingActionButton(
                                                onClick = { isSearchActive = true },
                                                modifier = Modifier
                                                    .padding(end = 16.dp, bottom = 12.dp)
                                                    .size(48.dp)
                                            ) {
                                                Icon(Icons.Default.Search, contentDescription = "Search")
                                            }
                                            FloatingActionButton(
                                                onClick = { showAddDialog = true },
                                                shape = CircleShape,
                                                modifier = Modifier
                                                    .size(74.dp)
                                                ) {
                                                Icon(Icons.Default.Add, contentDescription = "Add")
                                            }
                                        }
                                    }
                                }
                            },
                            bottomBar = {
                                if (isSearchActive) {
                                    Surface(
                                        color = Color.Transparent,
                                        shadowElevation = 0.dp,
                                        tonalElevation = 0.dp
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 16.dp, end = 16.dp, bottom = 48.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = searchQuery,
                                                onValueChange = { searchQuery = it },
                                                placeholder = { Text("Search notes...") },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(end = 8.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                singleLine = true,
                                                trailingIcon = {
                                                    IconButton(onClick = {
                                                        searchQuery = ""
                                                        isSearchActive = false
                                                    }) {
                                                        Icon(Icons.Default.Close, contentDescription = "Close search")
                                                    }
                                                },
                                                colors = TextFieldDefaults.colors(
                                                    focusedContainerColor = Color.Transparent,
                                                    unfocusedContainerColor = Color.Transparent,
                                                    disabledContainerColor = Color.Transparent
                                                ),
                                            )
                                        }
                                    }
                                }
                            }
                        ) { padding ->
                            Box(
                                Modifier
                                    .padding(padding)
                                    .fillMaxSize()
                            ) {

                                // Remember the grid state to scroll to top when sort type changes
                                val gridState = rememberLazyGridState()
                                LaunchedEffect(sortType) {
                                    gridState.scrollToItem(0)
                                }
                                val sortedNotes = when (sortType) {
                                    SortType.Newest -> notes.sortedByDescending { it.createdAt }
                                    SortType.Oldest -> notes.sortedBy { it.createdAt }
                                    SortType.NewlyEdited -> notes.sortedByDescending { it.lastUpdated }
                                    SortType.MostForgotten -> notes.sortedBy { it.score }
                                    SortType.LeastForgotten -> notes.sortedByDescending { it.score }
                                    SortType.LeastRecentlyDug -> notes.sortedByDescending { it.lastVisitedAt?.time ?: Long.MIN_VALUE }
                                    SortType.MostRecentlyDug -> notes.sortedBy { it.lastVisitedAt?.time ?: Long.MAX_VALUE }
//                                    SortType.LeastDug -> notes.sortedBy { note: Note -> note.visitTimestamps?.size ?: 0 }
//                                    SortType.MostDug -> notes.sortedByDescending { it.visitTimestamps?.size ?: 0 }
                                }
                                val filteredNotes = if (searchQuery.isBlank()) {
                                    sortedNotes
                                } else {
                                    sortedNotes.filter { note ->
                                        (note.text?.contains(searchQuery, ignoreCase = true) == true) ||
                                                (note.title?.contains(searchQuery, ignoreCase = true) == true) ||
                                                (note.saidWho?.contains(searchQuery, ignoreCase = true) == true) ||
                                                (note.tags?.any { it.contains(searchQuery, ignoreCase = true) } == true)
                                    }
                                }
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(minSize = 140.dp),
                                    state = gridState,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    items(filteredNotes, key = { it.id }) { note ->
                                        NoteCard(
                                            note = note,
                                            onClick = {
                                                scope.launch {
                                                    withContext(Dispatchers.IO) {
                                                        // Update score on note visit
                                                        noteRepository.logNoteVisitAndScore(note.id)
                                                        val updatedNote = db.noteDao().getById(note.id)
                                                        Log.d(
                                                            "NoteVisit",
                                                            "Note id: ${note.id} is visited. Visit count: ${updatedNote?.visitCount ?: "?"}"
                                                        )
                                                    }
                                                    showViewDialogId = note.id
                                                }
                                            },
                                            onDeleteClick = { showDeleteDialogId = note.id },
                                            backgroundColor = colorForNote(note),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // --- Dialogs: show only when requested, and always dismiss to a dialog-less state ---

                    // Add Note Dialog
                    if (showAddDialog) {
                        NoteDialog(
                            initialNote = Note(),
                            onSave = { newNote ->
                                scope.launch {
                                    showAddDialog = false
                                    withContext(Dispatchers.IO) {
                                        db.noteDao().insert(newNote)
                                    }
                                }
                            },
                            onDismiss = { showAddDialog = false },
                            noteDao = noteDao,
                            sourceBindingDao = sourceBindingDao,
                        )
                    }

                    // View Note Dialog
                    val noteToView = notes.find { it.id == showViewDialogId }
                    noteToView?.let { note ->
                        NoteViewDialog(
                            note = note,
                            onEdit = {
                                showViewDialogId = null
                                showEditDialogId = note.id
                            },
                            onDelete = { _ ->
                                showViewDialogId = null
                                showDeleteDialogId = note.id
                            },
                            onDismiss = { showViewDialogId = null },
                            onFilterByAuthor = { author ->
                                showViewDialogId = null
                                currentFilterType = FilterType.AUTHOR
                                currentFilterValue = author
                                showFilteredNotesScreen = true
                            },
                            onFilterByTitle = { title ->
                                showViewDialogId = null
                                currentFilterType = FilterType.TITLE
                                currentFilterValue = title
                                showFilteredNotesScreen = true
                            },
                            onFilterByTag = { tag ->
                                showViewDialogId = null
                                currentFilterType = FilterType.TAG
                                currentFilterValue = tag
                                showFilteredNotesScreen = true
                            }
                        )
                    }

                    // Edit Note Dialog
                    val noteToEdit = notes.find { it.id == showEditDialogId }
                    noteToEdit?.let { note ->
                        NoteDialog(
                            initialNote = note,
                            onSave = { updatedNote ->
                                scope.launch {
                                    showEditDialogId = null
                                    withContext(Dispatchers.IO) {
                                        db.noteDao().update(updatedNote)
                                    }
                                }
                            },
                            onDismiss = { showEditDialogId = null },
                            onCancel = {
                                showEditDialogId = null
                                showViewDialogId = note.id
                            },
                            noteDao = noteDao,
                            sourceBindingDao = sourceBindingDao,
                        )
                    }

                    // Delete Confirmation Dialog
                    val noteToDelete = notes.find { it.id == showDeleteDialogId }
                    noteToDelete?.let { note ->
                        DeleteNoteDialog(
                            onDelete = {
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        db.noteDao().delete(note)
                                    }
                                    showDeleteDialogId = null
                                    showEditDialogId = null
                                    showViewDialogId = null
                                }
                            },
                            onDismiss = { showDeleteDialogId = null },
                        )
                    }

                    // Settings Dialog
                    if (showSettingsDialog) {
                        SettingsDialog(
                            onDismiss = { showSettingsDialog = false },
                            onLaunch = topPageSetting!!,
                            onLaunchChange = { newSetting ->
                                scope.launch {
                                    preferencesManager.setTopPageSetting(newSetting)
                                }
                            },
                            themeType = themeType,
                            onThemeTypeChange = { newTheme ->
                                scope.launch {
                                    preferencesManager.setThemeType(newTheme)
                                }
                            }
                        )
                    }
                } // end else (preferences loaded)
            }
        }
    }
}

