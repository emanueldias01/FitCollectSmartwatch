package dev.emanueldias.fitcollectsmartwatch.presentation.initial

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.wear.compose.material3.*
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import dev.emanueldias.fitcollectsmartwatch.R
import dev.emanueldias.fitcollectsmartwatch.health.SensorPermissions
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.FitCollectSmartwatchTheme

@Composable
fun InitialScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToHistory: () -> Unit,
) {
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
                Icon(
                    painter = painterResource(R.drawable.outline_fitness_center_24),
                    contentDescription = "logo",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "FitCollect",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botão Play (Principal)
                    Button(
                        onClick = onNavigateToMain,
                        modifier = Modifier.size(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_play_arrow_24),
                            contentDescription = "Iniciar Treino",
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    FilledTonalButton(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_fitness_center_24),
                            contentDescription = "Ver Histórico",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                }
            } else {
                Text(
                    text = "Permissões necessárias para coletar dados de saúde.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { permissionLauncher.launch(SensorPermissions.required) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Permitir", fontSize = 12.sp)
                    }

                    FilledTonalButton(
                        onClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Config.", fontSize = 12.sp)
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
        InitialScreen(onNavigateToMain = {}, onNavigateToHistory = {})
    }
}