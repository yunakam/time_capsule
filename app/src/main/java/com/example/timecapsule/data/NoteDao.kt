package com.example.timecapsule.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    suspend fun getById(noteId: Long): Note?

    @Insert
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteById(noteId: Long)


    // Get suggestion as per user's input
    @Query("SELECT DISTINCT author FROM notes WHERE author LIKE :query || '%' ORDER BY author LIMIT 10")
    suspend fun getAuthorSuggestions(query: String): List<String>

    @Query("SELECT DISTINCT sourceTitle FROM notes WHERE sourceTitle LIKE :query || '%' ORDER BY sourceTitle LIMIT 10")
    suspend fun getTitleSuggestions(query: String): List<String>

    @Query("SELECT DISTINCT publisher FROM notes WHERE publisher LIKE :query || '%' ORDER BY publisher LIMIT 10")
    suspend fun getPublisherSuggestions(query: String): List<String>

    @Query("SELECT tags FROM notes")
    suspend fun getAllTagsRaw(): List<String?>


    // Get notes by key
    @Query("SELECT * FROM notes WHERE author = :author")
    suspend fun getNotesByAuthor(author: String): List<Note>

    @Query("SELECT * FROM notes WHERE sourceTitle = :title")
    suspend fun getNotesByTitle(title: String): List<Note>

    @Query("SELECT * FROM notes WHERE publisher = :publisher")
    suspend fun getNotesByPublisher(publisher: String): List<Note>

    @Query("SELECT * FROM notes WHERE tags LIKE '%' || :tag || '%'")
    suspend fun getNotesByTagRaw(tag: String): List<Note>
}
