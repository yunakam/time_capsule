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
import com.example.compose.AppTheme
import com.example.timecapsule.data.AppDatabase
import com.example.timecapsule.data.Note
import com.example.timecapsule.data.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    backgroundColor: Color
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val cleanedText = note.text
        .split("\n")
        .dropLastWhile { it.trim().isEmpty() }
        .joinToString("\n")
    val lines = cleanedText.lines()
    val previewText = if (lines.size > 5) lines.take(5).joinToString("\n") + "\n..." else cleanedText

    Card(
        modifier = Modifier
            .widthIn(min = 115.dp, max = 240.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onDeleteClick() }
                )
            }
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Column {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = previewText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = dateFormat.format(note.createdAt),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun DeleteNoteDialog(
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Note") },
        text = { Text(
            "Are you sure you want to delete this note?",
//            color = textColor
        ) },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text(
                    "Delete",
//                    color = buttonTextColor
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
//                    color = buttonTextColor
                )
            }
        },
//        containerColor = containerColor
    )
}


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getInstance(this)

        setContent {
            AppTheme(dynamicColor = false) {
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
                    AddNoteDialog(
                        onSave = { newNote ->
                            scope.launch {
                                showAddDialog = false
                                withContext(Dispatchers.IO) {
                                    db.noteDao().insert(newNote)
                                }
                            }
                        },
                        onDismiss = { showAddDialog = false }
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
                        onDelete = { noteToDelete ->
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
                    EditNoteDialog(
                        note = note,
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
                        onCancelToView = {
                            showEditDialogId = null
                            showViewDialogId = note.id
                        }
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
