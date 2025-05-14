package com.example.timecapsule.data

import java.util.Date

class NoteRepository(
    private val noteDao: NoteDao,
    private val noteVisitDao: NoteVisitDao
) {
    suspend fun logNoteVisit(noteId: Long) {
        val now = Date(System.currentTimeMillis())
        // Insert a new NoteVisit
        noteVisitDao.insert(NoteVisit(noteId = noteId, visitedAt = now))

        // Update Note's visitCount and lastVisitedAt
        val note = noteDao.getById(noteId) ?: return
        val updatedNote = note.copy(
            visitCount = note.visitCount + 1,
            lastVisitedAt = now
        )

        noteDao.update(updatedNote)
    }
}
