package com.example.timecapsule.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "notes")
@TypeConverters(Converters::class)
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String = "",
    val saidWho: String? = null, // author, speaker etc.
    val title: String? = null,
    val page: String? = null,
    val source: String? = null, // publisher, channel etc.
    val url: String? = null,
    val tags: List<String>? = null, // store as a list
    val category: NoteCategory? = null,

    val createdAt: Date = Date(),
    val lastVisitedAt: Date? = null,
    val visitCount: Int = 0,

    val score: Int = 100,
    val lastUpdated: Long = System.currentTimeMillis(),
    val visitTimestamps: List<Long> = emptyList()
)
