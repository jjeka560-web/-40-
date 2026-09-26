package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM workout_exercises ORDER BY orderIndex ASC, id ASC")
    fun getAllExercises(): Flow<List<WorkoutExercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: WorkoutExercise): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<WorkoutExercise>)

    @Update
    suspend fun update(exercise: WorkoutExercise)

    @Delete
    suspend fun delete(exercise: WorkoutExercise)

    @Query("DELETE FROM workout_exercises WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM workout_exercises")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM workout_exercises")
    suspend fun getCount(): Int
}
