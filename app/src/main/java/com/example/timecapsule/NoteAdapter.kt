package com.example.timecapsule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(
    private val notes: List<Note>,
    private val onNoteClick: (Note, Int) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)
        holder.itemView.setOnClickListener { onNoteClick(note, position) }
    }

    override fun getItemCount(): Int = notes.size

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val noteText: TextView = itemView.findViewById(R.id.noteText)
        private val noteDate: TextView = itemView.findViewById(R.id.noteDate)

        fun bind(note: Note) {
            noteText.text = note.text
            noteDate.text = note.date.toString()
        }
    }
}
