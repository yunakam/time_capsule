package com.example.timecapsule

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddNoteActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        db = AppDatabase.getInstance(this)

        val noteText: EditText = findViewById<EditText>(R.id.editTextNote)
        val noteAuthor: EditText = findViewById(R.id.noteAuthor)
        val noteSourceTitle: EditText = findViewById(R.id.noteSourceTitle)
        val noteSourceUrl: EditText = findViewById(R.id.noteSourceUrl)
        val notePage: EditText = findViewById(R.id.notePage)
        val notePublisher: EditText = findViewById(R.id.notePublisher)
        val noteTags: EditText = findViewById(R.id.noteTags)
        val saveButton: Button = findViewById(R.id.saveButton)

        val buttonClose: ImageButton = findViewById(R.id.closeButton)
        buttonClose.setOnClickListener {
            finish() // Closes the popup and returns to previous activity
        }

        saveButton.setOnClickListener {
            val text = noteText.text.toString()
            if (text.isBlank()) {
                Toast.makeText(this, "Note text is mandatory", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val author = noteAuthor.text.toString().ifBlank { null }
            val sourceTitle = noteSourceTitle.text.toString().ifBlank { null }
            val sourceUrl = noteSourceUrl.text.toString().ifBlank { null }
            val page = notePage.text.toString().ifBlank { null }
            val publisher = notePublisher.text.toString().ifBlank { null }
            val tagsInput = noteTags.text.toString()
            val tagsString = tagsInput.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .joinToString(",")
                .ifBlank { null }

            val newNote = Note(
                text = text,
                author = author,
                sourceTitle = sourceTitle,
                sourceUrl = sourceUrl,
                page = page,
                publisher = publisher,
                tags = tagsString
            )

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    db.noteDao().insert(newNote)
                }
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

        // Set popup window height
        val window = this.window
        val params = window.attributes
        params.height = (resources.displayMetrics.heightPixels * 0.8).toInt()
        window.attributes = params
    }
}
