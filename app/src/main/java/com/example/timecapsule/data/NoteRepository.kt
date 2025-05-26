package com.example.timecapsule.data

import java.util.Date

class NoteRepository(
    private val noteDao: NoteDao,
    private val noteVisitDao: NoteVisitDao
) {
    suspend fun logNoteVisitAndScore(noteId: Long) {
        val now = Date(System.currentTimeMillis())

        val note = noteDao.getById(noteId) ?: return

        // Update Note's score
        val noteScore = NoteScore(
            score = note.score,
            lastUpdated = note.lastUpdated,
            visitTimestamps = note.visitTimestamps.toMutableList() ?: mutableListOf()
        )
        noteScore.onVisited()

        // Update Note's visitCount and lastVisitedAt
        val updatedNote = note.copy(
            score = noteScore.calculateScore(),
            lastUpdated = noteScore.lastUpdated,
            visitTimestamps = noteScore.visitTimestamps,
            visitCount = note.visitCount + 1,
            lastVisitedAt = now
        )

        noteDao.update(updatedNote)
        // Insert a new NoteVisit
        noteVisitDao.insert(NoteVisit(noteId = noteId, visitedAt = now))
    }
}
