package dev.emanueldias.fitcollectsmartwatch.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Query("SELECT * FROM workouts ORDER BY startTimeMillis DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Int): WorkoutEntity?

    @Query("UPDATE workouts SET isSynced = :synced WHERE id = :id")
    suspend fun updateSyncStatus(id: Int, synced: Boolean)

    @androidx.room.Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)
}