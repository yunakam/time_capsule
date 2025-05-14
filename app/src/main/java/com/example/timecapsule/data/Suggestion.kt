package com.example.timecapsule.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suggestions")
data class Suggestion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // e.g., "author", "publisher", "tag"
    val value: String
)
