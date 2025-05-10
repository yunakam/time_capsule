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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.google.accompanist.flowlayout.FlowRow
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
                var showAddDialog by remember { mutableStateOf(false) }
                var showEditDialog by remember { mutableStateOf<Note?>(null) }
                var showDeleteDialog by remember { mutableStateOf<Note?>(null) }
                val scope = rememberCoroutineScope()
                var notes by remember { mutableStateOf(listOf<Note>()) }
                val context = LocalContext.current

                // Load notes from DB
                LaunchedEffect(Unit) {
                    notes = withContext(Dispatchers.IO) { db.noteDao().getAll() }
                }

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
                                NoteCard(
                                    note = note,
                                    onClick = { showEditDialog = note },
                                    onDeleteClick = { showDeleteDialog = note }
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
                                withContext(Dispatchers.IO) {
                                    db.noteDao().insert(newNote)
                                    notes = db.noteDao().getAll()
                                }
                                showAddDialog = false
                            }
                        },
                        onDismiss = { showAddDialog = false }
                    )
                }

                // Edit Note Dialog
                showEditDialog?.let { noteToEdit ->
                    EditNoteDialog(
                        note = noteToEdit,
                        onSave = { updatedNote ->
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    db.noteDao().update(updatedNote)
                                    notes = db.noteDao().getAll()
                                }
                                showEditDialog = null
                            }
                        },
                        onDelete = { noteToDelete ->
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    db.noteDao().delete(noteToDelete)
                                    notes = db.noteDao().getAll()
                                }
                                showEditDialog = null
                            }
                        },
                        onDismiss = { showEditDialog = null }
                    )
                }

                // Delete Confirmation Dialog
                showDeleteDialog?.let { noteToDelete ->
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = null },
                        title = { Text("Delete Note") },
                        text = { Text("Are you sure you want to delete this note?") },
                        confirmButton = {
                            TextButton(onClick = {
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        db.noteDao().delete(noteToDelete)
                                        notes = db.noteDao().getAll()
                                    }
                                    showDeleteDialog = null
                                }
                            }) { Text("Delete") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = null }) { Text("Cancel") }
                        }
                    )
                }
            }
        }
    }
}

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
