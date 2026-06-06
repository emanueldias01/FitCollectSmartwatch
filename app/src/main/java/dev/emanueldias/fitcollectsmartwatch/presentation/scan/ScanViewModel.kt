package dev.emanueldias.fitcollectsmartwatch.presentation.scan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class WatchUiState(
    val solicitacaoPendente: Boolean = false,
    val nomeDoCelularSolicitante: String = "",
    val idDoCelularSolicitante: String = "",
    val conectadoComSucesso: Boolean = false
)

class ScanViewModel(application: Application): AndroidViewModel(application), MessageClient.OnMessageReceivedListener {

    val _uiState = MutableStateFlow(WatchUiState())
    val uiState = _uiState.asStateFlow()

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

            _uiState.update {
                it.copy(
                    solicitacaoPendente = true,
                    nomeDoCelularSolicitante = deviceName,
                    idDoCelularSolicitante = deviceId
                )
            }
        }
    }

}
