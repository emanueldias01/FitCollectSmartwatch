package dev.emanueldias.fitcollectsmartwatch.presentation.simpleHeart

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dev.emanueldias.fitcollectsmartwatch.R
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.AndroidGreen
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.FitCollectSmartwatchTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SimpleHeartScreen(
    viewModel: SimpleHeartViewModel = viewModel(),
    onClickStop: () -> Unit,
) {

    val uiState by viewModel.currentBpm.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startMeasurer()
    }

    ScreenScaffold {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            EcgBackgroundAnimation()

            Text(
                text = if (uiState == 0.0) "--" else uiState.toInt().toString(),
                color = AndroidGreen,
                style = MaterialTheme.typography.displayMedium
            )

            Button(
                onClick = onClickStop,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_stop_24),
                    contentDescription = "stop"
                )
            }
        }
    }
}

@Composable
fun EcgBackgroundAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "EcgTransition")

    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "EcgProgress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerY = height / 2

        val ecgPath = Path().apply {
            moveTo(0f, centerY)
            lineTo(width * 0.2f, centerY)
            lineTo(width * 0.25f, centerY - 10f)
            lineTo(width * 0.3f, centerY)
            lineTo(width * 0.35f, centerY)
            lineTo(width * 0.38f, centerY + 15f)
            lineTo(width * 0.42f, centerY - 80f)
            lineTo(width * 0.46f, centerY + 30f)
            lineTo(width * 0.5f, centerY)
            lineTo(width * 0.6f, centerY)
            lineTo(width * 0.7f, centerY - 20f)
            lineTo(width * 0.78f, centerY)
            lineTo(width, centerY)
        }

        val pathMeasure = PathMeasure()
        pathMeasure.setPath(ecgPath, forceClosed = false)

        val partialPath = Path()

        val tailLength = pathMeasure.length * 0.3f
        val endDistance = pathMeasure.length * animationProgress
        val startDistance = (endDistance - tailLength).coerceAtLeast(0f)

        pathMeasure.getSegment(startDistance, endDistance, partialPath, startWithMoveTo = true)

        drawPath(
            path = partialPath,
            color = AndroidGreen.copy(alpha = 0.6f),
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}

@Preview
@WearPreviewDevices
@Composable
private fun SimpleHeathScreenPreview() {
    FitCollectSmartwatchTheme {
        SimpleHeartScreen(
            onClickStop = {}
        )
    }
}
