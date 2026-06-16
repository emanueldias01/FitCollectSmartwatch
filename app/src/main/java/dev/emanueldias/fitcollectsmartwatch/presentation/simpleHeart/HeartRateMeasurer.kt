package dev.emanueldias.fitcollectsmartwatch.presentation.simpleHeart

import android.content.Context
import android.util.Log
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.unregisterMeasureCallback
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class HeartRateMeasurer(context: Context) {

    private val measureClient = HealthServices.getClient(context).measureClient

    fun listenToHeartRate(): Flow<Double> = callbackFlow {

        val callback = object : MeasureCallback {
            override fun onAvailabilityChanged(
                dataType: DeltaDataType<*, *>,
                availability: Availability
            ) {
                Log.d("HeartRateMeasurer", "Availability changed: $availability")
            }

            override fun onDataReceived(data: DataPointContainer) {
                val heartRateDelta = data.getData(DataType.HEART_RATE_BPM)
                if (heartRateDelta.isNotEmpty()) {
                    val latestBpm = heartRateDelta.last().value
                    Log.d("HeartRateMeasurer", "BPM received: $latestBpm")
                    trySend(latestBpm)
                }
            }

            override fun onRegistered() {
                Log.d("HeartRateMeasurer", "Callback registered")
            }

            override fun onRegistrationFailed(throwable: Throwable) {
                Log.e("HeartRateMeasurer", "Registration failed", throwable)
                close(throwable)
            }
        }

        try {
            Log.d("HeartRateMeasurer", "Registering callback")
            measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM, callback)
        } catch (e: Exception) {
            Log.e("HeartRateMeasurer", "Exception during registration", e)
            close(e)
        }

        awaitClose {
            Log.d("HeartRateMeasurer", "Closing flow and unregistering callback")
            launch {
                try {
                    measureClient.unregisterMeasureCallback(DataType.HEART_RATE_BPM, callback)
                } catch (e: Exception) {
                    Log.e("HeartRateMeasurer", "Error unregistering callback", e)
                }
            }
        }
    }
}