package com.example.timecapsule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class NoteAdapter(
    private val notes: List<Note>,
    private val onDeleteClick: (Note) -> Unit,
    private val onNoteClick: (Note, Int) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteText: TextView = itemView.findViewById(R.id.noteText)
        val noteDate: TextView = itemView.findViewById(R.id.noteDate)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]

        // Remove trailing empty lines
        val cleanedText = note.text
            .split("\n")
            .dropLastWhile { it.trim().isEmpty() }
            .joinToString("\n")

        // Show only the first five lines
        val lines = cleanedText.lines()
        val previewText = if (lines.size > 5) {
            lines.take(5).joinToString("\n") + "\n..."
        } else {
            cleanedText
        }

        holder.noteText.text = previewText
        holder.noteDate.text = dateFormat.format(note.date)

        holder.deleteButton.setOnClickListener {
            onDeleteClick(note)
        }
        holder.itemView.setOnClickListener {
            onNoteClick(note, position)
        }
    }

    override fun getItemCount(): Int = notes.size
}
