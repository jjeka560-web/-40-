package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_session_records ORDER BY timestampMillis DESC")
    fun getAllSessions(): Flow<List<WorkoutSessionRecord>>

    @Query("SELECT * FROM workout_session_records WHERE dateEpochDay = :epochDay ORDER BY timestampMillis DESC")
    fun getSessionsForDay(epochDay: Long): Flow<List<WorkoutSessionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionRecord): Long

    @Delete
    suspend fun deleteSession(session: WorkoutSessionRecord)

    @Query("DELETE FROM workout_session_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM workout_session_records")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM workout_session_records")
    suspend fun getCount(): Int
}
