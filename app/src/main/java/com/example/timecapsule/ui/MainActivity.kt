package com.example.timecapsule.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

                val dummy_notes = remember {
                    mutableStateListOf(
                        Note(1, text = "Note from Alice", author = "Alice", sourceTitle = "Article A", tags = listOf("kotlin", "android")),
                        Note(2, text = "Note from Bob", author = "Bob", sourceTitle = "Article B", tags = listOf("compose", "android")),
                        Note(3, text = "Another note from Alice", author = "Alice", sourceTitle = "Article C", tags = listOf("kotlin", "ui")),
                    )
                }

                var showAddDialog by remember { mutableStateOf(true) }
                var showViewDialogId by remember { mutableStateOf<Long?>(null) }
                var showEditDialogId by remember { mutableStateOf<Long?>(null) }
                var showDeleteDialogId by remember { mutableStateOf<Long?>(null) }

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

                // This function needs access to 'notes' from the current scope
                // To ensure it always uses the latest 'notes', define it inside Composable or pass 'notes'
                val colorForNote: (Note) -> Color = { note ->
                    val currentVisitedNotes = notes.filter { it.lastVisitedAt != null }
                        .sortedByDescending { it.lastVisitedAt }
                    val currentVisitedCount = currentVisitedNotes.size
                    val currentBucketSize = (currentVisitedCount / colorBuckets.size.toFloat()).coerceAtLeast(1f)

                    if (note.lastVisitedAt == null) {
                        colorBuckets[0]  // new card
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
                        allNotes = notes, // Pass all notes from the database
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
                            FloatingActionButton(
                                onClick = { showAddDialog = true },
                                modifier = Modifier
                                    .offset(y = (-32).dp, x = (-32).dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }
                        }
                    ) { padding ->
                        Column(
                            Modifier
                                .padding(padding)
                                .fillMaxSize()
                        ) {
                            val sortedNotes = notes.sortedByDescending { it.createdAt }
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 140.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                            ) {
                                items(sortedNotes, key = { it.id }) { note ->
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
                                        backgroundColor = colorForNote(note)
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
                            showViewDialogId = null // Dismiss view dialog
                            showEditDialogId = note.id
                        },
                        onDelete = { _ ->
                            showViewDialogId = null // Dismiss view dialog if delete is chosen from here
                            showDeleteDialogId = note.id
                        },
                        onDismiss = { showViewDialogId = null },
                        onFilterByAuthor = { author ->
                            showViewDialogId = null // Dismiss view dialog
                            currentFilterType = FilterType.AUTHOR
                            currentFilterValue = author
                            showFilteredNotesScreen = true
                        },
                        onFilterByTitle = { title ->
                            showViewDialogId = null // Dismiss view dialog
                            currentFilterType = FilterType.SOURCE_TITLE
                            currentFilterValue = title
                            showFilteredNotesScreen = true
                        },
                        onFilterByTag = { tag ->
                            showViewDialogId = null // Dismiss view dialog
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
                            // Optionally, re-open view dialog if edit was cancelled
                            // showViewDialogId = note.id
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
                                showEditDialogId = null // Close edit dialog if delete was from there
                                showViewDialogId = null // Close view dialog if delete was from there
                            }
                        },
                        onDismiss = { showDeleteDialogId = null },
                    )
                }
            }
        }
    }
}