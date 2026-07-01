package dev.emanueldias.fitcollectsmartwatch.presentation.initial

import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.wear.compose.material3.*
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import dev.emanueldias.fitcollectsmartwatch.R
import dev.emanueldias.fitcollectsmartwatch.health.SensorPermissions
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.AndroidGreenDark
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.FitCollectSmartwatchTheme

@Composable
fun InitialScreen(onNavigateToMain: () -> Unit) {
    val context = LocalContext.current

    var isGranted by remember { mutableStateOf(false) }

    fun checkPermissions() {
        isGranted = SensorPermissions.hasAllRequired(context)
    }

    LaunchedEffect(Unit) { checkPermissions() }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { checkPermissions() }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { checkPermissions() }

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
                        onClick = { permissionLauncher.launch(SensorPermissions.required) },
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