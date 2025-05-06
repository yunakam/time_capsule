package com.example.timecapsule

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditNoteActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private var noteId: Long = -1L
    private var currentNote: Note? = null

    private lateinit var editText: EditText
    private lateinit var authorEditText: EditText
    private lateinit var sourceTitleEditText: EditText
    private lateinit var sourceUrlEditText: EditText
    private lateinit var pageEditText: EditText
    private lateinit var publisherEditText: EditText
    private lateinit var tagsEditText: EditText

    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        db = AppDatabase.getInstance(this)

        editText = findViewById<EditText>(R.id.editTextNote)
        authorEditText = findViewById(R.id.noteAuthor)
        sourceTitleEditText = findViewById(R.id.noteSourceTitle)
        sourceUrlEditText = findViewById(R.id.noteSourceUrl)
        pageEditText = findViewById(R.id.notePage)
        publisherEditText = findViewById(R.id.notePublisher)
        tagsEditText = findViewById(R.id.noteTags)

        saveButton = findViewById(R.id.saveButton)
        deleteButton = findViewById(R.id.deleteButton)

        noteId = intent.getLongExtra("note_id", -1L)
        if (noteId == -1L) {
            finish()
            return
        }

        loadNote()

        saveButton.setOnClickListener {
            saveNote()
        }

        deleteButton.setOnClickListener {
            confirmDelete()
        }

        val buttonClose: ImageButton = findViewById(R.id.closeButton)
        buttonClose.setOnClickListener {
            finish() // Closes the popup and returns to previous activity
        }

        // Set popup window height
        val window = this.window
        val params = window.attributes
        params.height = (resources.displayMetrics.heightPixels * 0.8).toInt()
        window.attributes = params
    }

    private fun loadNote() {
        lifecycleScope.launch {
            val note = withContext(Dispatchers.IO) {
                db.noteDao().getAll().find { it.id == noteId }
            }
            if (note == null) {
                finish()
                return@launch
            }
            currentNote = note
            editText.setText(note.text)
            authorEditText.setText(note.author ?: "")
            sourceTitleEditText.setText(note.sourceTitle ?: "")
            sourceUrlEditText.setText(note.sourceUrl ?: "")
            pageEditText.setText(note.page ?: "")
            publisherEditText.setText(note.publisher ?: "")
            tagsEditText.setText(note.tags ?: "")
        }
    }

    private fun saveNote() {
        val updatedNote = currentNote?.copy(
            text = editText.text.toString(),
            author = authorEditText.text.toString().ifBlank { null },
            sourceTitle = sourceTitleEditText.text.toString().ifBlank { null },
            sourceUrl = sourceUrlEditText.text.toString().ifBlank { null },
            page = pageEditText.text.toString().ifBlank { null },
            publisher = publisherEditText.text.toString().ifBlank { null },
            tags = tagsEditText.text.toString().ifBlank { null }
        ) ?: return

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.noteDao().update(updatedNote)
            }
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ -> deleteNote() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteNote() {
        val note = currentNote ?: return
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.noteDao().delete(note)
            }
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
