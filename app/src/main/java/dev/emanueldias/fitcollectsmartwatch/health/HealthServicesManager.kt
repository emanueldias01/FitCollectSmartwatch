package dev.emanueldias.fitcollectsmartwatch.health

import androidx.health.services.client.getCapabilities

import android.content.Context
import android.util.Log
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.unregisterMeasureCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

sealed class HeartRateMessage {
    data class Data(val bpm: Double) : HeartRateMessage()
    data class AvailabilityChanged(val availability: DataTypeAvailability) : HeartRateMessage()
}

class HealthServicesManager private constructor(context: Context) {

    private val measureClient = HealthServices.getClient(context.applicationContext).measureClient

    suspend fun hasHeartRateCapability(): Boolean = runCatching {
        val capabilities = measureClient.getCapabilities()
        DataType.HEART_RATE_BPM in capabilities.supportedDataTypesMeasure
    }.getOrDefault(false)

    fun heartRateMeasureFlow(): Flow<HeartRateMessage> = callbackFlow {
        val callback = object : MeasureCallback {
            override fun onAvailabilityChanged(
                dataType: DeltaDataType<*, *>,
                availability: Availability
            ) {
                if (availability is DataTypeAvailability) {
                    Log.d(TAG, "Disponibilidade: $availability")
                    trySend(HeartRateMessage.AvailabilityChanged(availability))
                }
            }

            override fun onDataReceived(data: DataPointContainer) {
                data.getData(DataType.HEART_RATE_BPM).lastOrNull()?.let { point ->
                    Log.d(TAG, "BPM recebido: ${point.value}")
                    trySend(HeartRateMessage.Data(point.value))
                }
            }

            override fun onRegistered() {
                Log.d(TAG, "Callback registrado com sucesso")
            }

            override fun onRegistrationFailed(throwable: Throwable) {
                Log.e(TAG, "Falha ao registrar — provavelmente falta permissão", throwable)
                close(throwable)
            }
        }

        Log.d(TAG, "Registrando callback de frequência cardíaca")
        measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM, callback)

        awaitClose {
            Log.d(TAG, "Cancelando registro do callback")

            CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                runCatching {
                    measureClient.unregisterMeasureCallback(DataType.HEART_RATE_BPM, callback)
                }
            }
        }
    }

    companion object {
        private const val TAG = "HealthServicesManager"

        @Volatile
        private var INSTANCE: HealthServicesManager? = null

        fun getInstance(context: Context): HealthServicesManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HealthServicesManager(context.applicationContext).also { INSTANCE = it }
            }
    }
}