package dev.emanueldias.fitcollectsmartwatch.presentation.simpleHeart

import android.app.Application
import androidx.health.services.client.data.DataTypeAvailability
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.emanueldias.fitcollectsmartwatch.health.HealthServicesManager
import dev.emanueldias.fitcollectsmartwatch.health.HeartRateMessage
import dev.emanueldias.fitcollectsmartwatch.health.SensorPermissions
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HeartRateUiState(
    val bpm: Double = 0.0,
    val availability: DataTypeAvailability = DataTypeAvailability.UNKNOWN,
    val supported: Boolean = true,
    val hasPermission: Boolean = true,
)

class SimpleHeartViewModel(application: Application) : AndroidViewModel(application) {

    private val healthServicesManager = HealthServicesManager.getInstance(application)

    private val _uiState = MutableStateFlow(HeartRateUiState())
    val uiState = _uiState.asStateFlow()

    private var measureJob: Job? = null

    fun startMeasurer() {
        if (measureJob?.isActive == true) return

        if (!SensorPermissions.hasHeartRate(getApplication())) {
            _uiState.value = _uiState.value.copy(hasPermission = false)
            return
        }

        measureJob = viewModelScope.launch {
            val supported = healthServicesManager.hasHeartRateCapability()
            _uiState.value = _uiState.value.copy(supported = supported, hasPermission = true)
            if (!supported) return@launch

            healthServicesManager.heartRateMeasureFlow().collect { message ->
                _uiState.value = when (message) {
                    is HeartRateMessage.Data -> _uiState.value.copy(
                        bpm = message.bpm,
                        availability = DataTypeAvailability.AVAILABLE
                    )
                    is HeartRateMessage.AvailabilityChanged ->
                        _uiState.value.copy(availability = message.availability)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        measureJob?.cancel()
    }
}