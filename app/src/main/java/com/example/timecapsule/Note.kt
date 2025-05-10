package com.example.timecapsule

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "notes")
@TypeConverters(Converters::class)
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val author: String? = null,
    val sourceTitle: String? = null,
    val sourceUrl: String? = null,
    val page: String? = null,
    val publisher: String? = null,
    val tags: String? = null, // store as comma-separated string
    val date: Date = Date()
)
