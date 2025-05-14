package com.example.timecapsule.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "note_visits",
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE // delete visits if note is deleted
        )
    ],
    indices = [Index("noteId")]
)
data class NoteVisit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val noteId: Long,
    val visitedAt: Date = Date()
)
