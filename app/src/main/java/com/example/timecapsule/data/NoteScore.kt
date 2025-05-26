package com.example.timecapsule.data

import kotlin.math.max
import kotlin.math.min

/**
 * Represents the score of a note, which can be updated based on visits and time decay.
 * The score starts at 100, can go up to 150, and decays over time.
 */
class NoteScore(
    var score: Int = 100,
    var lastUpdated: Long = System.currentTimeMillis(),
    val visitTimestamps: MutableList<Long> = mutableListOf()
) {
    companion object {
        private const val MAX_SCORE = 150
        private const val MIN_SCORE = 0
        private const val MAX_RECOVERY_POINTS = 10
        private const val MIN_RECOVERY_POINTS = 1
        private const val DECAY_PER_DAY = 1
        private const val RECOVERY_POINTS_WINDOW_DAYS = 30L
    }

    fun calculateScore(): Int {
        updateScore()
        return score
    }

    fun onVisited() {
        updateScore()
        val now = System.currentTimeMillis()
        cleanupOldVisits(now)
        val visitsInXDays = visitTimestamps.size  // X: RECOVERY_POINTS_WINDOW_DAYS
        val recoveryPoints = max(MAX_RECOVERY_POINTS - visitsInXDays, MIN_RECOVERY_POINTS)
        score = min(score + recoveryPoints, MAX_SCORE)
        visitTimestamps.add(now)
        lastUpdated = now
    }

    private fun updateScore() {
        val now = System.currentTimeMillis()
        val daysPassed = (now - lastUpdated) / (1000 * 60 * 60 * 24)
        if (daysPassed > 0) {
            score = max(score - (daysPassed * DECAY_PER_DAY).toInt(), MIN_SCORE)
            lastUpdated += daysPassed * (1000 * 60 * 60)
        }
        cleanupOldVisits(now)
    }

    private fun cleanupOldVisits(now: Long) {
        val recoveryPointsWindowDaysMillis = RECOVERY_POINTS_WINDOW_DAYS * 24 * 60 * 60 * 1000
        visitTimestamps.removeAll { it < now - recoveryPointsWindowDaysMillis }
    }
}
