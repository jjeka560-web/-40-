package com.example.model

data class RoutineExercise(
    val name: String,
    val targetMuscle: String,
    val sets: String,
    val repsOrTime: String,
    val restSec: Int,
    val executionTip: String,
    val biomechanicsNote: String
)

data class WorkoutRoutine(
    val type: WorkoutType,
    val title: String,
    val targetGoal: String,
    val durationMinutes: Int,
    val intensity: String,
    val exercises: List<RoutineExercise>
)
