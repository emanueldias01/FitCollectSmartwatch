package dev.emanueldias.fitcollectsmartwatch.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutData(
    val sport: String,
    val startTime: Long,
    val endTime: Long,
    val durationSeconds: Long,
    val heartRateMeasurements: List<HeartRateMeasurement>
)

@Serializable
data class HeartRateMeasurement(
    val timestamp: Long,
    val bpm: Double
)