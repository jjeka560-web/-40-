package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_session_records")
data class WorkoutSessionRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workoutTitle: String,
    val workoutType: String,
    val dateEpochDay: Long,
    val timestampMillis: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val totalRounds: Int = 1,
    val notes: String = ""
)
