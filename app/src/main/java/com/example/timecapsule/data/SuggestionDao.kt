package com.example.timecapsule.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SuggestionDao {
    @Query("SELECT * FROM suggestions WHERE type = :type AND value LIKE :query || '%' ORDER BY value LIMIT 10")
    suspend fun getSuggestions(type: String, query: String): List<Suggestion>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(suggestion: Suggestion)
}
