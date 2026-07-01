package dev.emanueldias.fitcollectsmartwatch.presentation.sport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SportUiState {
    object Idle : SportUiState()
    data class Countdown(val seconds: Int) : SportUiState()
    data class Running(val elapsedTimeSeconds: Long) : SportUiState()
    data class Paused(val elapsedTimeSeconds: Long) : SportUiState()
}

class SportViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<SportUiState>(SportUiState.Idle)
    val uiState: StateFlow<SportUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startTime: Long = 0
    private var accumulatedTime: Long = 0

    fun startCountdown() {
        viewModelScope.launch {
            for (i in 5 downTo 1) {
                _uiState.value = SportUiState.Countdown(i)
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
                _uiState.value = SportUiState.Running(accumulatedTime + currentSessionTime)
                delay(1000)
            }
        }
    }

    fun pauseTimer() {
        val currentState = _uiState.value
        if (currentState is SportUiState.Running) {
            timerJob?.cancel()
            accumulatedTime += (System.currentTimeMillis() - startTime) / 1000
            _uiState.value = SportUiState.Paused(accumulatedTime)
        }
    }

    fun resumeTimer() {
        if (_uiState.value is SportUiState.Paused) {
            startTimer()
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        accumulatedTime = 0
        _uiState.value = SportUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
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
