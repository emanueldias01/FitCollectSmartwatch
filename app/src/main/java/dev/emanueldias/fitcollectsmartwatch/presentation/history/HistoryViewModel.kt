package dev.emanueldias.fitcollectsmartwatch.presentation.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.emanueldias.fitcollectsmartwatch.data.local.AppDatabase
import dev.emanueldias.fitcollectsmartwatch.data.local.WorkoutEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

data class HistoryUiState(
    val workouts: List<WorkoutEntity> = emptyList(),
    val isLoading: Boolean = true
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()

    val uiState: StateFlow<HistoryUiState> = workoutDao.getAllWorkouts()
        .map { HistoryUiState(workouts = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HistoryUiState(isLoading = true)
        )

    fun deleteWorkout(workout: WorkoutEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            // Deleta o arquivo JSON
            val file = File(workout.dataFilePath)
            if (file.exists()) {
                file.delete()
            }
            // Deleta do banco
            workoutDao.deleteWorkout(workout)
        }
    }
        
    fun formatDuration(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) {
            String.format("%02dh %02dm %02ds", h, m, s)
        } else if (m > 0) {
            String.format("%02dm %02ds", m, s)
        } else {
            String.format("%02ds", s)
        }
    }
}