package com.example.timecapsule

import AddNoteDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
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
            .widthIn(min = 120.dp, max = 240.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onDeleteClick() }
                )
            }
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Box(modifier = Modifier.padding(12.dp)) {
            Column {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateFormat.format(note.date),
                    style = MaterialTheme.typography.labelSmall,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = previewText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getInstance(this)

        setContent {
            AppTheme(dynamicColor = false) {
                var showAddDialog by remember { mutableStateOf(false) }
                var showEditDialogId by remember { mutableStateOf<Long?>(null) }
                var showDeleteDialogId by remember { mutableStateOf<Long?>(null) }
                val scope = rememberCoroutineScope()
                val notesFlow = remember { db.noteDao().getAllFlow() }
                val notes by notesFlow.collectAsState(initial = emptyList())

                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                ) { padding ->
                    Column(
                        Modifier
                            .padding(padding)
                            .fillMaxSize()
                    ) {
                        FlowRow(
                            mainAxisSpacing = 12.dp,
                            crossAxisSpacing = 12.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            notes.forEach { note ->
                                androidx.compose.runtime.key(note.id) {
                                    NoteCard(
                                        note = note,
                                        onClick = { showEditDialogId = note.id },
                                        onDeleteClick = { showDeleteDialogId = note.id }
                                    )
                                }
                            }
                        }
                    }
                }

                // Add Note Dialog
                if (showAddDialog) {
                    AddNoteDialog(
                        onSave = { newNote ->
                            scope.launch {
                                // Clear dialog state first!
                                showAddDialog = false
                                withContext(Dispatchers.IO) {
                                    db.noteDao().insert(newNote)
                                }
                            }
                        },
                        onDismiss = { showAddDialog = false }
                    )
                }

                // Edit Note Dialog
                val noteToEdit = notes.find { it.id == showEditDialogId }
                if (showEditDialogId != null && noteToEdit == null) {
                    showEditDialogId = null
                }
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
                        onDelete = { noteToDelete ->
                            scope.launch {
                                showEditDialogId = null
                                withContext(Dispatchers.IO) {
                                    db.noteDao().delete(noteToDelete)
                                }
                            }
                        },
                        onDismiss = { showEditDialogId = null }
                    )
                }

                // Delete Confirmation Dialog
                val noteToDelete = notes.find { it.id == showDeleteDialogId }
                noteToDelete?.let { note ->
                    AlertDialog(
                        onDismissRequest = { showDeleteDialogId = null },
                        title = { Text("Delete Note") },
                        text = { Text("Are you sure you want to delete this note?") },
                        confirmButton = {
                            TextButton(onClick = {
                                scope.launch {
                                    // Clear dialog state first!
                                    showDeleteDialogId = null
                                    withContext(Dispatchers.IO) {
                                        db.noteDao().delete(note)
                                    }
                                }
                            }) { Text("Delete") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialogId = null }) { Text("Cancel") }
                        }
                    )
                }
            }
        }
    }
}
