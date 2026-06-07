package dev.emanueldias.fitcollectsmartwatch.presentation.scan

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import dev.emanueldias.fitcollectsmartwatch.R
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.AndroidGreen
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.AndroidGreenDark
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.AndroidGreenLight

@Composable
fun ScanScreen(
    viewModel: ScanViewModel = viewModel(),
    onClickAccept: () -> Unit = {}
) {

    val uiState = viewModel.uiState.collectAsState().value

    DisposableEffect(Unit) {
        viewModel.registerListener()
        onDispose {
            viewModel.removeListener()
        }
    }

    ScreenScaffold {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            PulseAnimation()

            if(uiState.solicitationPending) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Solicitação de conexão")
                    Text(uiState.deviceName)
                    Text("Aceitar?")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(onClick = {
                            viewModel.sendConnectionAck(true)
                            onClickAccept()
                        }) {
                            Icon(painter = painterResource(R.drawable.outline_check_small_24), contentDescription = "accept")
                        }
                        Button(onClick = { viewModel.sendConnectionAck(false) }) {
                            Icon(painter = painterResource(R.drawable.outline_close_24), contentDescription = "reject")
                        }
                    }

                }
            } else {
                Text(text = "Aguardando conexão")
            }
        }
    }
}

@Composable
fun PulseAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "PulseTransition")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    val pulseGradient = Brush.radialGradient(
        colors = listOf(
            AndroidGreenLight.copy(alpha = alpha),
            AndroidGreen.copy(alpha = alpha * 0.7f),
            AndroidGreenDark.copy(alpha = 0.0f)
        )
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale)
            .background(brush = pulseGradient, shape = CircleShape)
    )
}