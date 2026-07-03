package dev.emanueldias.fitcollectsmartwatch.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dev.emanueldias.fitcollectsmartwatch.R
import dev.emanueldias.fitcollectsmartwatch.health.HealthServicesManager
import dev.emanueldias.fitcollectsmartwatch.health.HeartRateMessage
import dev.emanueldias.fitcollectsmartwatch.presentation.sport.SportPhase
import dev.emanueldias.fitcollectsmartwatch.presentation.sport.SportUiState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SportService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var timerJob: Job? = null
    private var measureJob: Job? = null

    private val _uiState = MutableStateFlow(SportUiState())
    val uiState = _uiState.asStateFlow()

    private lateinit var healthServicesManager: HealthServicesManager
    private var startTime: Long = 0
    private var accumulatedTime: Long = 0

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): SportService = this@SportService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        healthServicesManager = HealthServicesManager.getInstance(applicationContext)
        createNotificationChannel()
    }

    fun startSport() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, createNotification("Sport em andamento..."), ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH)
        } else {
            startForeground(1, createNotification("Sport em andamento..."))
        }
        startTime = System.currentTimeMillis()
        timerJob?.cancel()
        timerJob = serviceScope.launch {
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
        measureJob = serviceScope.launch {
            val supported = healthServicesManager.hasHeartRateCapability()
            if (!supported) return@launch
            healthServicesManager.heartRateMeasureFlow().collect { message ->
                _uiState.value = when (message) {
                    is HeartRateMessage.Data -> _uiState.value.copy(bpm = message.bpm)
                    else -> _uiState.value
                }
            }
        }
    }

    fun pauseSport() {
        timerJob?.cancel()
        measureJob?.cancel()
        accumulatedTime += (System.currentTimeMillis() - startTime) / 1000
        _uiState.value = _uiState.value.copy(phase = SportPhase.Paused)
        stopForeground(STOP_FOREGROUND_DETACH)
    }

    fun stopSport() {
        serviceScope.coroutineContext.cancelChildren()
        accumulatedTime = 0
        _uiState.value = SportUiState()
        stopSelf()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel("sport_channel", "Sport Tracking", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun createNotification(content: String): Notification {
        return NotificationCompat.Builder(this, "sport_channel")
            .setContentTitle("FitCollect")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Certifique-se que o ícone existe
            .setOngoing(true)
            .build()
    }
}