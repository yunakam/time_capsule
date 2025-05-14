package com.example.timecapsule.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.Date

@Dao
interface NoteVisitDao {
    @Insert
    suspend fun insert(visit: NoteVisit): Long

    @Query("SELECT * FROM note_visits WHERE noteId = :noteId ORDER BY visitedAt DESC")
    suspend fun getVisitsForNote(noteId: Long): List<NoteVisit>

    @Query("SELECT COUNT(*) FROM note_visits WHERE noteId = :noteId")
    suspend fun getVisitCount(noteId: Long): Int

    @Query("SELECT MAX(visitedAt) FROM note_visits WHERE noteId = :noteId")
    suspend fun getLastVisit(noteId: Long): Date?
}
