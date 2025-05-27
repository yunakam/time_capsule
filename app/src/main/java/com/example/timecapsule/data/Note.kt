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
    // val author: String? = null,
    val saidWho: String? = null,
    //val sourceTitle: String? = null,
    val title: String? = null,
    val source: String? = null,
    //val sourceUrl: String? = null,
    val url: String? = null,
    val page: String? = null,
    //val publisher: String? = null,
    val tags: List<String>? = null, // store as a list

    val createdAt: Date = Date(),
    val lastVisitedAt: Date? = null,
    val visitCount: Int = 0,

    val score: Int = 100,
    val lastUpdated: Long = System.currentTimeMillis(),
    val visitTimestamps: List<Long> = emptyList()
)
