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
    @Query("SELECT DISTINCT saidWho FROM notes WHERE saidWho LIKE :query || '%' ORDER BY saidWho LIMIT 10")
    suspend fun getSaidWhoSuggestions(query: String): List<String>

    @Query("SELECT DISTINCT title FROM notes WHERE title LIKE :query || '%' ORDER BY title LIMIT 10")
    suspend fun getTitleSuggestions(query: String): List<String>

    @Query("SELECT DISTINCT source FROM notes WHERE source LIKE :query || '%' ORDER BY source LIMIT 10")
    suspend fun getSourceSuggestions(query: String): List<String>

    @Query("SELECT tags FROM notes")
    suspend fun getAllTagsRaw(): List<String?>


    // Get notes by key
    @Query("SELECT * FROM notes WHERE saidWho = :saidWho")
    suspend fun getNotesByAuthor(saidWho: String): List<Note>

    @Query("SELECT * FROM notes WHERE title = :title")
    suspend fun getNotesByTitle(title: String): List<Note>

    @Query("SELECT * FROM notes WHERE source = :source")
    suspend fun getNotesByPublisher(source: String): List<Note>

    @Query("SELECT * FROM notes WHERE tags LIKE '%' || :tag || '%'")
    suspend fun getNotesByTagRaw(tag: String): List<Note>
}
