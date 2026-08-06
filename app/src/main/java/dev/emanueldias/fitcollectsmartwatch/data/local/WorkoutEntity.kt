package dev.emanueldias.fitcollectsmartwatch.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.emanueldias.fitcollectsmartwatch.data.model.Sport

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sport: Sport,
    val durationSeconds: Long,
    val averageHeartRate: Double,
    val dataFilePath: String,
    val startTimeMillis: Long,
    val endTimeMillis: Long
)