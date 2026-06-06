package dev.emanueldias.fitcollectsmartwatch.presentation.scan

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.AndroidGreen
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.AndroidGreenDark
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.AndroidGreenLight

@Composable
fun ScanScreen() {
    ScreenScaffold {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            PulseAnimation()

            Text(text = "Aguardando conexão")
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