package dev.emanueldias.fitcollectsmartwatch.presentation.initial

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.wear.compose.material3.*
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import dev.emanueldias.fitcollectsmartwatch.R
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.AndroidGreenDark
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.FitCollectSmartwatchTheme

@Composable
fun InitialScreen(onNavigateToMain: () -> Unit) {
    val context = LocalContext.current

    val permissions = remember {
        val basePermissions = arrayOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
        if (android.os.Build.VERSION.SDK_INT >= 36) {
            basePermissions + "android.permission.health.READ_HEART_RATE"
        } else {
            basePermissions
        }
    }

    var isGranted by remember {
        mutableStateOf(false)
    }

    fun checkPermissions() {
        val hasSensors = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BODY_SENSORS
        ) == PackageManager.PERMISSION_GRANTED
        val hasActivity = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED

        val hasHealthHeartRate = if (android.os.Build.VERSION.SDK_INT >= 36) {
            ContextCompat.checkSelfPermission(
                context,
                "android.permission.health.READ_HEART_RATE"
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        Log.d("SENSORS", "$hasSensors $hasActivity $hasHealthHeartRate")

        isGranted = hasActivity && hasHealthHeartRate
    }

    LaunchedEffect(Unit) {
        checkPermissions()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        checkPermissions()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        checkPermissions()
    }

    ScreenScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isGranted) {
                Image(
                    painter = painterResource(R.drawable.outline_fitness_center_24),
                    contentDescription = "logo",
                    modifier = Modifier.size(60.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "FitCollect", color = AndroidGreenDark, fontSize = 16.sp)
                Spacer(modifier = Modifier.size(16.dp))

                Button(
                    onClick = onNavigateToMain,
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_play_arrow_24),
                        contentDescription = "init scan"
                    )
                }
            } else {
                Text(
                    text = "Sensores e Atividade Física são necessários.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = AndroidGreenDark,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.size(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Button(
                        onClick = { permissionLauncher.launch(permissions) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AndroidGreenDark)
                    ) {
                        Text("Permitir", fontSize = 12.sp, textAlign = TextAlign.Center)
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.filledTonalButtonColors()
                    ) {
                        Text("Config.", fontSize = 12.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}


@WearPreviewDevices
@Composable
private fun InitialScreenPreview() {
    FitCollectSmartwatchTheme {
        InitialScreen(onNavigateToMain = {})
    }
}