package dev.emanueldias.fitcollectsmartwatch.health


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object SensorPermissions {

    private const val HEALTH_READ_HEART_RATE = "android.permission.health.READ_HEART_RATE"

    private val heartRatePermission: String
        get() = if (Build.VERSION.SDK_INT >= 36) {
            HEALTH_READ_HEART_RATE
        } else {
            Manifest.permission.BODY_SENSORS
        }

    val required: Array<String>
        get() = arrayOf(heartRatePermission, Manifest.permission.ACTIVITY_RECOGNITION)

    fun hasHeartRate(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, heartRatePermission) ==
                PackageManager.PERMISSION_GRANTED

    fun hasActivityRecognition(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) ==
                PackageManager.PERMISSION_GRANTED

    fun hasAllRequired(context: Context): Boolean =
        hasHeartRate(context) && hasActivityRecognition(context)
}