package com.example.model

enum class AtlasDiagramType {
    SWING,
    CLEAN,
    PRESS,
    SNATCH,
    SQUAT,
    LUNGE,
    DEADLIFT,
    GRIP_WRIST,
    CORE_ABS,
    NECK,
    ROTATION_HALO,
    THROW,
    ISOMETRIC_HOLD,
    TURKISH_GETUP,
    THRUSTER,
    STRETCHING
}

data class AtlasExercise(
    val number: Int,
    val name: String,
    val group: String,
    val initialPose: String,
    val steps: List<String>,
    val targetMuscles: String,
    val breathingTip: String,
    val biomechanicsNote: String,
    val historicalRecord: String? = null,
    val diagramType: AtlasDiagramType
)
