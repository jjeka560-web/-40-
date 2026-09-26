package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_exercises")
data class WorkoutExercise(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val sets: Int = 3,
    val reps: Int = 10,
    val restSeconds: Int = 90,
    val category: String = "Загальне",
    val orderIndex: Int = 0
)
