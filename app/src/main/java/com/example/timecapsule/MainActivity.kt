package com.example.timecapsule

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.compose.AppTheme
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getInstance(this)

        setContent {
            AppTheme(dynamicColor = false) {
                val coroutineScope = rememberCoroutineScope()
                var reloadNotes by remember { mutableStateOf(false) }
                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == RESULT_OK) {
                        reloadNotes = !reloadNotes
                    }
                }
                NoteFlowScreen(
                    db = db,
                    onAddNote = {
                        val intent = Intent(this, AddNoteActivity::class.java)
                        launcher.launch(intent)
                    },
                    onEditNote = { note ->
                        val intent = Intent(this, EditNoteActivity::class.java)
                        intent.putExtra("note_id", note.id)
                        startActivity(intent)
                    },
                    reloadTrigger = reloadNotes
                )
            }
        }
    }
}

@Composable
fun NoteFlowScreen(
    db: AppDatabase,
    onAddNote: () -> Unit,
    onEditNote: (Note) -> Unit,
    reloadTrigger: Boolean
) {
    var notes by remember { mutableStateOf(listOf<Note>()) }
    var showDialog by remember { mutableStateOf<Pair<Boolean, Note?>>(false to null) }
    val context = LocalContext.current

    // Load notes from db
    LaunchedEffect(reloadTrigger) {
        notes = withContext(Dispatchers.IO) { db.noteDao().getAll() }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
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
                        onClick = { onEditNote(note) },
                        onDeleteClick = { showDialog = true to note }
                    )
                }
            }
        }

        // Delete confirmation dialog
        if (showDialog.first && showDialog.second != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false to null },
                title = { Text("Delete Note") },
                text = { Text("Are you sure you want to delete this note?") },
                confirmButton = {
                    TextButton(onClick = {
                        val noteToDelete = showDialog.second!!
                        (context as? ComponentActivity)?.lifecycleScope?.launch {
                            withContext(Dispatchers.IO) { db.noteDao().delete(noteToDelete) }
                            notes = notes.filter { it.id != noteToDelete.id }
                        }
                        showDialog = false to null
                    }) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false to null }) { Text("Cancel") }
                }
            )
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
            .widthIn(min = 100.dp, max = 240.dp)
            .clickable { onClick() }
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
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .size(20.dp)
                    .align(androidx.compose.ui.Alignment.TopEnd)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
