package com.example.data

import kotlinx.coroutines.flow.Flow

class ExerciseRepository(private val exerciseDao: ExerciseDao) {

    val allExercises: Flow<List<WorkoutExercise>> = exerciseDao.getAllExercises()

    suspend fun insert(exercise: WorkoutExercise) = exerciseDao.insert(exercise)

    suspend fun update(exercise: WorkoutExercise) = exerciseDao.update(exercise)

    suspend fun delete(exercise: WorkoutExercise) = exerciseDao.delete(exercise)

    suspend fun deleteById(id: Int) = exerciseDao.deleteById(id)

    suspend fun clearAll() = exerciseDao.clearAll()

    suspend fun seedDefaultsIfEmpty() {
        if (exerciseDao.getCount() == 0) {
            val defaults = listOf(
                WorkoutExercise(name = "Мах гирею двома руками", sets = 4, reps = 20, restSeconds = 60, category = "Спина", orderIndex = 1),
                WorkoutExercise(name = "Кубкові присідання (Goblet Squat)", sets = 3, reps = 12, restSeconds = 90, category = "Ноги", orderIndex = 2),
                WorkoutExercise(name = "Турецький підйом", sets = 3, reps = 3, restSeconds = 60, category = "Прес та Кор", orderIndex = 3),
                WorkoutExercise(name = "Прогулянка фермера (раунди)", sets = 4, reps = 1, restSeconds = 60, category = "Прес та Кор", orderIndex = 4),
                WorkoutExercise(name = "Жим гирі дном догори (Bottoms-Up)", sets = 3, reps = 6, restSeconds = 90, category = "Плечі", orderIndex = 5),
                WorkoutExercise(name = "Обертання навколо голови (Halo)", sets = 3, reps = 10, restSeconds = 45, category = "Плечі", orderIndex = 6)
            )
            exerciseDao.insertAll(defaults)
        }
    }

    suspend fun resetToDefaultPlan() {
        exerciseDao.clearAll()
        seedDefaultsIfEmpty()
    }
}
