package dev.emanueldias.fitcollectsmartwatch.data.sync

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import dev.emanueldias.fitcollectsmartwatch.data.model.WorkoutData
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WearSyncManager(context: Context) {
    private val dataClient: DataClient = Wearable.getDataClient(context)

    suspend fun syncWorkout(workoutData: WorkoutData): Boolean {
        return try {
            val jsonString = Json.encodeToString(workoutData)
            val request = PutDataMapRequest.create("/workout/${workoutData.startTime}").run {
                dataMap.putString("workout_json", jsonString)
                dataMap.putLong("timestamp", System.currentTimeMillis())
                asPutDataRequest()
            }
            
            request.setUrgent()
            
            dataClient.putDataItem(request).await()
            Log.d("WearSyncManager", "Treino sincronizado com sucesso: ${workoutData.startTime}")
            true
        } catch (e: Exception) {
            Log.e("WearSyncManager", "Erro ao sincronizar treino", e)
            false
        }
    }
}
