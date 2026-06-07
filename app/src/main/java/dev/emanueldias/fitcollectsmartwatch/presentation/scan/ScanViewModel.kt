package dev.emanueldias.fitcollectsmartwatch.presentation.scan

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


data class WatchUiState(
    val solicitationPending: Boolean = false,
    val deviceName: String = "",
    val deviceId: String = "",
    val connectedWithSucess: Boolean = false,
    val error: String? = null
)

class ScanViewModel(application: Application): AndroidViewModel(application), MessageClient.OnMessageReceivedListener {

    val _uiState = MutableStateFlow(WatchUiState())
    val uiState = _uiState.asStateFlow()

    private val TAG = "SCAN_VIEWMODEL"

    private val messageClient = Wearable.getMessageClient(application)

    fun registerListener() {
        messageClient.addListener(this)
    }

    fun removeListener() {
        messageClient.removeListener(this)
    }

    override fun onMessageReceived(event: MessageEvent) {
        if (event.path == "/connection_request") {
            val deviceName = String(event.data)
            val deviceId = event.sourceNodeId

            Log.d(TAG, "deviceName: $deviceName, deviceId: $deviceId")
            _uiState.update {
                it.copy(
                    solicitationPending = true,
                    deviceName = deviceName,
                    deviceId = deviceId
                )
            }
        }
    }

    fun sendConnectionAck(accept: Boolean) {
        val deviceId = _uiState.value.deviceId

        Log.d(TAG, "sendConnectionAck: $deviceId")

        _uiState.update { it.copy(solicitationPending = false) }

        if (accept && deviceId.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    messageClient.sendMessage(
                        deviceId,
                        "/connection_accept",
                        "ACK".toByteArray()
                    ).await()

                    _uiState.update { it.copy(connectedWithSucess = true) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = e.message) }
                }
            }
        }
    }

}
