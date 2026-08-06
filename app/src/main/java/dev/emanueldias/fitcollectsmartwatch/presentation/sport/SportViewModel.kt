package dev.emanueldias.fitcollectsmartwatch.presentation.sport

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.health.services.client.data.DataTypeAvailability
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.emanueldias.fitcollectsmartwatch.data.model.Sport
import dev.emanueldias.fitcollectsmartwatch.services.SportService
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

    private var sportService: SportService? = null
    private var isBound = false
    private var currentSport: Sport? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as SportService.LocalBinder
            sportService = binder.getService()
            isBound = true

            viewModelScope.launch {
                sportService?.uiState?.collect { serviceState ->
                    _uiState.value = serviceState
                }
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            sportService = null
            isBound = false
        }
    }

    init {
        Intent(application, SportService::class.java).also { intent ->
            application.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    fun startCountdown(sport: Sport) {
        currentSport = sport
        viewModelScope.launch {
            for (i in 5 downTo 1) {
                _uiState.value = _uiState.value.copy(
                    phase = SportPhase.Countdown,
                    countdownSeconds = i
                )
                delay(1000)
            }
            startSportService()
        }
    }

    private fun startSportService() {
        val sport = currentSport ?: return
        val intent = Intent(getApplication(), SportService::class.java)
        getApplication<Application>().startForegroundService(intent)
        sportService?.startSport(sport)
    }

    fun pauseTimer() {
        sportService?.pauseSport()
    }

    fun resumeTimer() {
        val sport = currentSport ?: return
        sportService?.startSport(sport)
    }

    fun stopTimer() {
        sportService?.stopSport()
        val intent = Intent(getApplication(), SportService::class.java)
        getApplication<Application>().stopService(intent)
    }

    override fun onCleared() {
        super.onCleared()
        if (isBound) {
            getApplication<Application>().unbindService(connection)
            isBound = false
        }
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
