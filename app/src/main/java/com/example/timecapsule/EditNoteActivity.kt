package com.example.timecapsule

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditNoteActivity : ComponentActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getInstance(this)
        val noteId = intent.getLongExtra("note_id", -1L)
        if (noteId == -1L) {
            finish()
            return
        }

        setContent {
            var note by remember { mutableStateOf<Note?>(null) }
            var loading by remember { mutableStateOf(true) }
            var showDeleteDialog by remember { mutableStateOf(false) }

            // Load the note from DB
            LaunchedEffect(noteId) {
                loading = true
                note = withContext(Dispatchers.IO) {
                    db.noteDao().getAll().find { it.id == noteId }
                }
                loading = false
                if (note == null) finish()
            }

            note?.let { loadedNote ->
                EditNoteScreen(
                    note = loadedNote,
                    onSave = { updatedNote ->
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                db.noteDao().update(updatedNote)
                            }
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    },
                    onDelete = {
                        showDeleteDialog = true
                    },
                    onClose = { finish() },
                    showDeleteDialog = showDeleteDialog,
                    onConfirmDelete = {
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                db.noteDao().delete(loadedNote)
                            }
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    },
                    onDismissDeleteDialog = { showDeleteDialog = false }
                )
            }
        }
    }
}
