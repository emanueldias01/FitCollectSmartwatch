package dev.emanueldias.fitcollectsmartwatch.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import dev.emanueldias.fitcollectsmartwatch.R
import dev.emanueldias.fitcollectsmartwatch.data.local.AppDatabase
import dev.emanueldias.fitcollectsmartwatch.data.local.WorkoutEntity
import dev.emanueldias.fitcollectsmartwatch.data.model.HeartRateMeasurement
import dev.emanueldias.fitcollectsmartwatch.data.model.Sport
import dev.emanueldias.fitcollectsmartwatch.data.model.WorkoutData
import dev.emanueldias.fitcollectsmartwatch.health.HealthServicesManager
import dev.emanueldias.fitcollectsmartwatch.health.HealthMessage
import dev.emanueldias.fitcollectsmartwatch.presentation.MainActivity
import dev.emanueldias.fitcollectsmartwatch.presentation.sport.SportPhase
import dev.emanueldias.fitcollectsmartwatch.presentation.sport.SportUiState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class SportService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var timerJob: Job? = null
    private var measureJob: Job? = null

    private val _uiState = MutableStateFlow(SportUiState())
    val uiState = _uiState.asStateFlow()

    private lateinit var healthServicesManager: HealthServicesManager
    private var startTime: Long = 0
    private var accumulatedTime: Long = 0
    private var sessionStartTime: Long = 0
    
    private val measurements = mutableListOf<HeartRateMeasurement>()
    private var lastMeasurementTimestamp: Long = 0
    private var currentSport: Sport? = null
    private var currentDistanceMeters: Double = 0.0
    private var currentCalories: Double = 0.0

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

    fun prepareSport(sport: Sport) {
        serviceScope.launch {
            healthServicesManager.prepareExercise(sport)
        }
    }

    fun startSport(sport: Sport) {
        if (currentSport == null) {
            currentSport = sport
            sessionStartTime = System.currentTimeMillis()
            measurements.clear()
            currentDistanceMeters = 0.0
            currentCalories = 0.0
        }
        
        val notification = createNotification("Coleta em andamento...")
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH)
        } else {
            startForeground(1, notification)
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
                
                updateOngoingActivity(sport)
                
                delay(1000)
            }
        }
        startMeasurement(sport)
    }

    private fun updateOngoingActivity(sport: Sport) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, "sport_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)

        val ongoingActivity = OngoingActivity.Builder(applicationContext, 1, notificationBuilder)
            .setAnimatedIcon(R.drawable.outline_play_arrow_24)
            .setStaticIcon(R.drawable.outline_play_arrow_24)
            .setTouchIntent(pendingIntent)
            .setStatus(
                Status.Builder()
                    .addPart("text", Status.TextPart(sport.displayName))
                    .build()
            )
            .build()
        
        ongoingActivity.apply(applicationContext)
    }

    private fun startMeasurement(sport: Sport) {
        measureJob?.cancel()
        measureJob = serviceScope.launch {
            healthServicesManager.exerciseFlow(sport).collect { message ->
                when (message) {
                    is HealthMessage.HeartRate -> {
                        _uiState.value = _uiState.value.copy(bpm = message.bpm)
                        
                        val now = System.currentTimeMillis()
                        if (now - lastMeasurementTimestamp >= 30000) {
                            measurements.add(HeartRateMeasurement(now, message.bpm))
                            lastMeasurementTimestamp = now
                        }
                    }
                    is HealthMessage.Distance -> {
                        currentDistanceMeters = message.meters
                        _uiState.value = _uiState.value.copy(distanceMeters = currentDistanceMeters)
                    }
                    is HealthMessage.Calories -> {
                        currentCalories = message.kcal
                        _uiState.value = _uiState.value.copy(calories = currentCalories)
                    }
                    is HealthMessage.AvailabilityChanged -> {
                        _uiState.value = _uiState.value.copy(availability = message.availability)
                    }
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
        val endTime = System.currentTimeMillis()
        val totalTime = accumulatedTime + (if (startTime > 0 && _uiState.value.phase == SportPhase.Running) (endTime - startTime) / 1000 else 0)
        
        serviceScope.launch {
            saveWorkout(endTime, totalTime)
            
            withContext(Dispatchers.Main) {
                serviceScope.coroutineContext.cancelChildren()
                accumulatedTime = 0
                startTime = 0
                currentSport = null
                measurements.clear()
                lastMeasurementTimestamp = 0
                currentDistanceMeters = 0.0
                currentCalories = 0.0
                _uiState.value = SportUiState()
                stopSelf()
            }
        }
    }

    private suspend fun saveWorkout(endTime: Long, totalTimeSeconds: Long) {
        val sport = currentSport ?: return
        if (measurements.isEmpty()) return

        val avgHeartRate = measurements.map { it.bpm }.average()
        
        val workoutData = WorkoutData(
            sport = sport.name,
            startTime = sessionStartTime,
            endTime = endTime,
            durationSeconds = totalTimeSeconds,
            distanceMeters = currentDistanceMeters,
            calories = currentCalories,
            heartRateMeasurements = measurements.toList()
        )

        val jsonString = Json.encodeToString(workoutData)
        val fileName = "workout_${sessionStartTime}.json"
        val file = File(applicationContext.filesDir, fileName)
        
        try {
            file.writeText(jsonString)
            
            val workoutEntity = WorkoutEntity(
                sport = sport,
                durationSeconds = totalTimeSeconds,
                averageHeartRate = avgHeartRate,
                distanceMeters = currentDistanceMeters,
                calories = currentCalories,
                dataFilePath = file.absolutePath,
                startTimeMillis = sessionStartTime,
                endTimeMillis = endTime
            )
            
            AppDatabase.getDatabase(applicationContext).workoutDao().insertWorkout(workoutEntity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel("sport_channel", "Sport Tracking", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun createNotification(content: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        
        return NotificationCompat.Builder(this, "sport_channel")
            .setContentTitle("FitCollect")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }
}
