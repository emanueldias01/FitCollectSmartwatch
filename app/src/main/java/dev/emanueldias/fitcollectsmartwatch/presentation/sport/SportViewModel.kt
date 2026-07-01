package dev.emanueldias.fitcollectsmartwatch.presentation.sport

import android.app.Application
import androidx.health.services.client.data.DataTypeAvailability
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.emanueldias.fitcollectsmartwatch.health.HealthServicesManager
import dev.emanueldias.fitcollectsmartwatch.health.HeartRateMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SportPhase {
    Idle, Countdown, Running, Paused
}

data class SportUiState(
    val phase: SportPhase = SportPhase.Idle,
    val bpm: Double = 0.0,
    val elapsedTimeSeconds: Long = 0,
    val countdownSeconds: Int = 0,
    val isSupported: Boolean = false,
    val hasPermission: Boolean = false,
    val availability: DataTypeAvailability = DataTypeAvailability.UNKNOWN
)

class SportViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SportUiState())
    val uiState: StateFlow<SportUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var measureJob: Job? = null

    private val healthServicesManager = HealthServicesManager.getInstance(application)
    private var startTime: Long = 0
    private var accumulatedTime: Long = 0

    fun startCountdown() {
        viewModelScope.launch {
            for (i in 5 downTo 1) {
                _uiState.value = _uiState.value.copy(
                    phase = SportPhase.Countdown,
                    countdownSeconds = i
                )
                delay(1000)
            }
            startTimer()
        }
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val currentSessionTime = (System.currentTimeMillis() - startTime) / 1000
                _uiState.value = _uiState.value.copy(
                    phase = SportPhase.Running,
                    elapsedTimeSeconds = accumulatedTime + currentSessionTime
                )
                delay(1000)
            }
        }
        
        startMeasurement()
    }

    private fun startMeasurement() {
        measureJob?.cancel()
        measureJob = viewModelScope.launch {
            val supported = healthServicesManager.hasHeartRateCapability()
            _uiState.value = _uiState.value.copy(isSupported = supported, hasPermission = true)
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

    fun pauseTimer() {
        if (_uiState.value.phase == SportPhase.Running) {
            timerJob?.cancel()
            measureJob?.cancel()
            accumulatedTime += (System.currentTimeMillis() - startTime) / 1000
            _uiState.value = _uiState.value.copy(
                phase = SportPhase.Paused,
                elapsedTimeSeconds = accumulatedTime
            )
        }
    }

    fun resumeTimer() {
        if (_uiState.value.phase == SportPhase.Paused) {
            startTimer()
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        measureJob?.cancel()
        accumulatedTime = 0
        startTime = 0
        _uiState.value = SportUiState()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        measureJob?.cancel()
    }

    fun formatTime(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) {
            String.format("%02d:%02d:%02d", h, m, s)
        } else {
            String.format("%02d:%02d", m, s)
        }
    }
}
