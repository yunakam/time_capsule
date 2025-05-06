package com.example.timecapsule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val noteList = mutableListOf<Note>()
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(this)

        // RecyclerView setup
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        noteAdapter = NoteAdapter(noteList, this::onNoteClick)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = noteAdapter

        // Add Note button
        val addNoteButton: FloatingActionButton = findViewById(R.id.addNoteButton)
        addNoteButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivityForResult(intent, ADD_NOTE_REQUEST)
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun loadNotes() {
        lifecycleScope.launch {
            val notes = withContext(Dispatchers.IO) { db.noteDao().getAll() }
            noteList.clear()
            noteList.addAll(notes)
            noteAdapter.notifyDataSetChanged()
        }
    }

    private fun onNoteClick(note: Note, position: Int) {
        val intent = Intent(this, EditNoteActivity::class.java)
        intent.putExtra("note_id", note.id)
        startActivityForResult(intent, EDIT_NOTE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            // No need to handle data directly; just reload notes from DB
            loadNotes()
        }
    }

    companion object {
        const val ADD_NOTE_REQUEST = 1
        const val EDIT_NOTE_REQUEST = 2
    }
}
