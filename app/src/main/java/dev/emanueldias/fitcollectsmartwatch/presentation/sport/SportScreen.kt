package dev.emanueldias.fitcollectsmartwatch.presentation.sport

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import dev.emanueldias.fitcollectsmartwatch.R
import dev.emanueldias.fitcollectsmartwatch.data.model.Sport

private enum class SportPhase {
    Idle, Countdown, Running, Paused
}

@Composable
fun SportScreen(
    type: Sport,
    viewModel: SportViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val currentPhase = when (uiState) {
        is SportUiState.Idle -> SportPhase.Idle
        is SportUiState.Countdown -> SportPhase.Countdown
        is SportUiState.Running -> SportPhase.Running
        is SportUiState.Paused -> SportPhase.Paused
    }

    ScreenScaffold {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = currentPhase,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "SportStateAnimation"
            ) { phase ->
                when (phase) {
                    SportPhase.Idle -> IdleView(type, onStart = viewModel::startCountdown)

                    SportPhase.Countdown -> {
                        val seconds = (uiState as? SportUiState.Countdown)?.seconds ?: 0
                        CountdownView(seconds)
                    }

                    SportPhase.Running -> {
                        val seconds = (uiState as? SportUiState.Running)?.elapsedTimeSeconds ?: 0L
                        TimerView(
                            type = type,
                            time = viewModel.formatTime(seconds),
                            isPaused = false,
                            onPause = viewModel::pauseTimer,
                            onStop = {
                                viewModel.stopTimer()
                                onNavigateBack()
                            }
                        )
                    }

                    SportPhase.Paused -> {
                        val seconds = (uiState as? SportUiState.Paused)?.elapsedTimeSeconds ?: 0L
                        TimerView(
                            type = type,
                            time = viewModel.formatTime(seconds),
                            isPaused = true,
                            onResume = viewModel::resumeTimer,
                            onStop = {
                                viewModel.stopTimer()
                                onNavigateBack()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IdleView(type: Sport, onStart: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(type.iconRes),
            contentDescription = type.displayName,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = type.displayName,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onStart,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_play_arrow_24),
                contentDescription = "Start"
            )
        }
    }
}

@Composable
private fun CountdownView(seconds: Int) {
    Box(contentAlignment = Alignment.Center) {
        Text(
            text = seconds.toString(),
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun TimerView(
    type: Sport,
    time: String,
    isPaused: Boolean,
    onPause: () -> Unit = {},
    onResume: () -> Unit = {},
    onStop: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(type.iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = time,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isPaused) {
                Button(onClick = onResume) {
                    Icon(
                        painter = painterResource(R.drawable.outline_play_arrow_24),
                        contentDescription = "Resume"
                    )
                }
            } else {
                Button(
                    onClick = onPause,
                    colors = ButtonDefaults.filledTonalButtonColors()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_pause_24),
                        contentDescription = "Pause"
                    )
                }
            }
            Button(
                onClick = onStop,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_stop_24),
                    contentDescription = "Stop"
                )
            }
        }
    }
}