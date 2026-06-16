package dev.emanueldias.fitcollectsmartwatch.presentation.simpleHeart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SimpleHeartViewModel(application: Application): AndroidViewModel(application) {

    private val heartRateMeasurer = HeartRateMeasurer(application)

    private val _currentBpm = MutableStateFlow(0.0)
    val currentBpm = _currentBpm.asStateFlow()

    private var measureJob: kotlinx.coroutines.Job? = null

    fun startMeasurer() {
        if (measureJob?.isActive == true) return

        measureJob = viewModelScope.launch {
            heartRateMeasurer.listenToHeartRate().collect {
                _currentBpm.value = it
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        measureJob?.cancel()
    }
}