package com.example.timecapsule.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.example.timecapsule.data.AppDatabase
import com.example.timecapsule.data.Note
import com.example.timecapsule.data.NoteRepository
import com.example.timecapsule.ui.components.DeleteNoteDialog
import com.example.timecapsule.ui.components.NoteCard
import com.example.timecapsule.ui.components.NoteDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(this)

        setContent {
            AppTheme(dynamicColor = false) {

                var showFilteredDialog by remember { mutableStateOf(false) }
                var filteredCategory by remember { mutableStateOf<FilterCategory?>(null) }
                var filteredValue by remember { mutableStateOf("") }

                val navController = rememberNavController()

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

                fun colorForNote(note: Note): Color {
                    return if (note.lastVisitedAt == null) {
                        colorBuckets[0]  // new card
                    } else {
                        val recencyIndex = visitedNotes.indexOfFirst { it.id == note.id }
                        if (recencyIndex == -1) colorBuckets.last() else {
                            val bucket = (recencyIndex / bucketSize).toInt().coerceIn(0, colorBuckets.lastIndex)
                            colorBuckets[bucket]
                        }
                    }
                }

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
                        noteDao = noteDao
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
                            scope.launch {
                                showDeleteDialogId = note.id
                            }
                        },
                        onDismiss = { showViewDialogId = null }
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
                        // there is no delete option in EditNoteDialog currently
//                        onDelete = { noteToDelete ->
//                            scope.launch {
//                                showDeleteDialogId = note.id
//                            }
//                        },
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
                                // Close both Delete and Edit dialogs
                                showDeleteDialogId = null
                                showEditDialogId = null
                            }
                        },
                        onDismiss = { showDeleteDialogId = null },  // only close DeleteDialog
                    )
                }
            }
        }
    }
}
