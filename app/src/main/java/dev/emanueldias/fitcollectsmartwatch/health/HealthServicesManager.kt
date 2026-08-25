package dev.emanueldias.fitcollectsmartwatch.health

import android.content.Context
import android.util.Log
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.WarmUpConfig
import androidx.health.services.client.getCapabilities
import androidx.health.services.client.prepareExercise
import androidx.health.services.client.startExercise
import androidx.health.services.client.endExercise
import androidx.health.services.client.unregisterMeasureCallback
import dev.emanueldias.fitcollectsmartwatch.data.model.Sport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

sealed class HealthMessage {
    data class HeartRate(val bpm: Double) : HealthMessage()
    data class Distance(val meters: Double) : HealthMessage()
    data class Calories(val kcal: Double) : HealthMessage()
    data class AvailabilityChanged(val availability: DataTypeAvailability) : HealthMessage()
}

class HealthServicesManager private constructor(context: Context) {

    private val healthServicesClient = HealthServices.getClient(context.applicationContext)
    private val measureClient = healthServicesClient.measureClient
    private val exerciseClient = healthServicesClient.exerciseClient

    suspend fun hasHeartRateCapability(): Boolean = runCatching {
        val capabilities = measureClient.getCapabilities()
        DataType.HEART_RATE_BPM in capabilities.supportedDataTypesMeasure
    }.getOrDefault(false)

    fun heartRateMeasureFlow(): Flow<HealthMessage> = callbackFlow {
        val callback = object : MeasureCallback {
            override fun onAvailabilityChanged(
                dataType: DeltaDataType<*, *>,
                availability: Availability
            ) {
                if (availability is DataTypeAvailability) {
                    trySend(HealthMessage.AvailabilityChanged(availability))
                }
            }

            override fun onDataReceived(data: DataPointContainer) {
                data.getData(DataType.HEART_RATE_BPM).lastOrNull()?.let { point ->
                    trySend(HealthMessage.HeartRate(point.value))
                }
            }

            override fun onRegistered() {}
            override fun onRegistrationFailed(throwable: Throwable) {
                close(throwable)
            }
        }

        measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM, callback)

        awaitClose {
            CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                runCatching {
                    measureClient.unregisterMeasureCallback(DataType.HEART_RATE_BPM, callback)
                }
            }
        }
    }

    suspend fun prepareExercise(sport: Sport) {
        val exerciseType = getExerciseType(sport)
        val deltaTypes = mutableSetOf<DeltaDataType<*, *>>(
            DataType.HEART_RATE_BPM,
            DataType.CALORIES
        )
        if (sport.tracksDistance) {
            deltaTypes.add(DataType.DISTANCE)
        }

        val warmUpConfig = WarmUpConfig(exerciseType, deltaTypes)
        
        try {
            exerciseClient.prepareExercise(warmUpConfig)
            Log.d("HealthServicesManager", "Sensores aquecendo para: ${sport.displayName}")
        } catch (e: Exception) {
            Log.e("HealthServicesManager", "Erro ao preparar sensores", e)
        }
    }

    fun exerciseFlow(sport: Sport): Flow<HealthMessage> = callbackFlow {
        var accumulatedDistance = 0.0
        var accumulatedCalories = 0.0
        
        val callback = object : ExerciseUpdateCallback {
            override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
                val hr = update.latestMetrics.getData(DataType.HEART_RATE_BPM).lastOrNull()
                if (hr != null) {
                    trySend(HealthMessage.HeartRate(hr.value))
                }

                val distDelta = update.latestMetrics.getData(DataType.DISTANCE).lastOrNull()
                if (distDelta != null) {
                    accumulatedDistance += distDelta.value
                    trySend(HealthMessage.Distance(accumulatedDistance))
                }

                val calDelta = update.latestMetrics.getData(DataType.CALORIES).lastOrNull()
                if (calDelta != null) {
                    accumulatedCalories += calDelta.value
                    trySend(HealthMessage.Calories(accumulatedCalories))
                }
            }

            override fun onAvailabilityChanged(dataType: DataType<*, *>, availability: Availability) {
                if (availability is DataTypeAvailability && dataType == DataType.HEART_RATE_BPM) {
                    trySend(HealthMessage.AvailabilityChanged(availability))
                }
            }

            override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) {}
            override fun onRegistered() {}
            override fun onRegistrationFailed(throwable: Throwable) {
                close(throwable)
            }
        }

        val exerciseType = getExerciseType(sport)
        val dataTypes = mutableSetOf<DataType<*, *>>(
            DataType.HEART_RATE_BPM,
            DataType.CALORIES
        )
        if (sport.tracksDistance) {
            dataTypes.add(DataType.DISTANCE)
        }

        val config = ExerciseConfig.builder(exerciseType)
            .setDataTypes(dataTypes)
            .setIsGpsEnabled(sport.tracksDistance)
            .build()

        exerciseClient.setUpdateCallback(callback)
        
        launch {
            try {
                exerciseClient.startExercise(config)
            } catch (e: Exception) {
                Log.e("HealthServicesManager", "Erro ao iniciar exercício", e)
            }
        }

        awaitClose {
            CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                runCatching {
                    exerciseClient.endExercise()
                }
            }
        }
    }

    private fun getExerciseType(sport: Sport): ExerciseType {
        return when (sport) {
            Sport.RUNNING -> ExerciseType.RUNNING
            Sport.WALKING -> ExerciseType.WALKING
            Sport.CYCLING -> ExerciseType.BIKING
            Sport.SWIMMING -> ExerciseType.SWIMMING_POOL
            Sport.HIKING -> ExerciseType.HIKING
            Sport.GYM -> ExerciseType.WEIGHTLIFTING
            Sport.ELLIPTICAL -> ExerciseType.ELLIPTICAL
            Sport.GYMNASTICS -> ExerciseType.GYMNASTICS
            Sport.TREADMILL -> ExerciseType.RUNNING_TREADMILL
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: HealthServicesManager? = null

        fun getInstance(context: Context): HealthServicesManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HealthServicesManager(context.applicationContext).also { INSTANCE = it }
            }
    }
}
