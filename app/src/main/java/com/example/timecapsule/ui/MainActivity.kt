package com.example.timecapsule.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.example.timecapsule.data.AppDatabase
import com.example.timecapsule.data.FilterType
import com.example.timecapsule.data.Note
import com.example.timecapsule.data.NoteRepository
import com.example.timecapsule.ui.components.DeleteNoteDialog
import com.example.timecapsule.ui.components.NoteCard
import com.example.timecapsule.ui.components.NoteDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(this)

        setContent {
            AppTheme(dynamicColor = false) {

                var showFilteredNotesScreen by remember { mutableStateOf(false) }
                var currentFilterType by remember { mutableStateOf<FilterType?>(null) }
                var currentFilterValue by remember { mutableStateOf<String?>(null) }

                var showAddDialog by remember { mutableStateOf(true) }
                var showViewDialogId by remember { mutableStateOf<Long?>(null) }
                var showEditDialogId by remember { mutableStateOf<Long?>(null) }
                var showDeleteDialogId by remember { mutableStateOf<Long?>(null) }

                var isSearchActive by remember { mutableStateOf(false) }
                var searchQuery by remember { mutableStateOf("") }

                val scope = rememberCoroutineScope()
                val notesFlow = remember { db.noteDao().getAllFlow() }
                val notes by notesFlow.collectAsState(initial = emptyList())
                val noteRepository = remember {
                    NoteRepository(db.noteDao(), db.noteVisitDao())
                }

                val noteDao = db.noteDao()


                // Color bucket logic: computed per recomposition
                val sortedNotes = notes.sortedByDescending { it.createdAt }
                val colorBuckets = listOf(
                    MaterialTheme.colorScheme.surfaceContainerLow,
                    MaterialTheme.colorScheme.surfaceContainer,
                    MaterialTheme.colorScheme.surfaceContainerHigh,
                    MaterialTheme.colorScheme.surfaceContainerHighest
                )
                val visitedNotes = notes.filter { it.lastVisitedAt != null } // filter out the newly created note
                    .sortedByDescending { it.lastVisitedAt }
                val visitedCount = visitedNotes.size
                val bucketSize = (visitedCount / colorBuckets.size.toFloat()).coerceAtLeast(1f)

                // Give access to 'notes' from the current scope
                // to ensure it always uses the latest 'notes', define it inside Composable or pass 'notes'
                val colorForNote: (Note) -> Color = { note ->
                    val currentVisitedNotes = notes.filter { it.lastVisitedAt != null }
                        .sortedByDescending { it.lastVisitedAt }
                    val currentVisitedCount = currentVisitedNotes.size
                    val currentBucketSize = (currentVisitedCount / colorBuckets.size.toFloat()).coerceAtLeast(1f)

                    if (note.lastVisitedAt == null) {
                        colorBuckets[0]
                    } else {
                        val recencyIndex = currentVisitedNotes.indexOfFirst { it.id == note.id }
                        if (recencyIndex == -1) colorBuckets.last() else {
                            val bucket = (recencyIndex / currentBucketSize).toInt().coerceIn(0, colorBuckets.lastIndex)
                            colorBuckets[bucket]
                        }
                    }
                }
//                fun colorForNote(note: Note): Color {
//                    return if (note.lastVisitedAt == null) {
//                        colorBuckets[0]  // new card
//                    } else {
//                        val recencyIndex = visitedNotes.indexOfFirst { it.id == note.id }
//                        if (recencyIndex == -1) colorBuckets.last() else {
//                            val bucket = (recencyIndex / bucketSize).toInt().coerceIn(0, colorBuckets.lastIndex)
//                            colorBuckets[bucket]
//                        }
//                    }
//                }


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
                                    noteRepository.logNoteVisit(note.id)
                                }
                                showViewDialogId = note.id
                            }
                        },
                        onNoteDeleteClick = { note ->
                            showDeleteDialogId = note.id
                        },
                        colorForNote = colorForNote
                    )
                } else {

                    // Main screen content
                    Scaffold(
                        floatingActionButton = {
                            if (!isSearchActive) {
                                Row(
                                    modifier = Modifier.padding(end = 16.dp, bottom = 36.dp)
                                ) {
                                    FloatingActionButton(
                                        onClick = { isSearchActive = true },
                                        modifier = Modifier.padding(end = 16.dp)
                                    ) {
                                        Icon(Icons.Default.Search, contentDescription = "Search")
                                    }
                                    FloatingActionButton(
                                        onClick = { showAddDialog = true }
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Add")
                                    }
                                }
                            }
                        },
                        bottomBar = {
                            if (isSearchActive) {
                                // The search bar at the bottom
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
                                        }
                                    )
                                }
                            }
                        }
                    ) { padding ->
                        Box(
                            Modifier
                                .padding(padding)
                                .fillMaxSize()
                                .then(
                                    if (isSearchActive) Modifier.pointerInput(Unit) {
                                        detectTapGestures {
                                            isSearchActive = false
                                        }
                                    } else Modifier
                                )
                        ) {
                            val filteredNotes = if (searchQuery.isBlank()) {
                                sortedNotes
                            } else {
                                sortedNotes.filter { note ->
                                    (note.text?.contains(searchQuery, ignoreCase = true) == true) ||
                                            (note.sourceTitle?.contains(searchQuery, ignoreCase = true) == true) ||
                                            (note.author?.contains(searchQuery, ignoreCase = true) == true) ||
                                            (note.tags?.any { it.contains(searchQuery, ignoreCase = true) } == true)
                                }
                            }
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 140.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                            ) {
                                items(filteredNotes, key = { it.id }) { note ->
                                    NoteCard(
                                        note = note,
                                        onClick = {
                                            scope.launch {
                                                withContext(Dispatchers.IO) {
                                                    noteRepository.logNoteVisit(note.id)
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
                                        modifier = Modifier.pointerInput(Unit) {} // Consume click
                                    )
                                }
                            }
                        }

                    }
                }

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
                            currentFilterType = FilterType.SOURCE_TITLE
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
                        noteDao = noteDao
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
            }
        }
    }
}