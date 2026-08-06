package dev.emanueldias.fitcollectsmartwatch.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.*
import dev.emanueldias.fitcollectsmartwatch.R
import dev.emanueldias.fitcollectsmartwatch.data.local.WorkoutEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberScalingLazyListState()
    val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
    
    var workoutToDelete by remember { mutableStateOf<WorkoutEntity?>(null) }

    ScreenScaffold(scrollState = listState) {
        AlertDialog(
            visible = workoutToDelete != null,
            onDismissRequest = { workoutToDelete = null },
            confirmButton = {
                Button(
                    onClick = {
                        workoutToDelete?.let { viewModel.deleteWorkout(it) }
                        workoutToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                ) {
                    Icon(painterResource(R.drawable.outline_check_small_24), "Confirmar")
                }
            },
            dismissButton = {
                FilledTonalButton(
                    onClick = { workoutToDelete = null },
                ) {
                    Icon(painterResource(R.drawable.outline_close_24), "Cancelar")
                }
            },
            title = { Text("Excluir?", textAlign = TextAlign.Center) },
            text = { Text("Deseja apagar esta coleta?", textAlign = TextAlign.Center) }
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            ScalingLazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(
                    top = 32.dp,
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 32.dp
                )
            ) {
                item {
                    ListHeader {
                        Text("Histórico")
                    }
                }

                if (uiState.workouts.isEmpty()) {
                    item {
                        Text(
                            text = "Nenhuma coleta encontrada",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                } else {
                    items(uiState.workouts) { workout ->
                        TitleCard(
                            onClick = { /* Opcional: ver detalhes */ },
                            onLongClick = { workoutToDelete = workout },
                            title = { Text(workout.sport.displayName) },
                            subtitle = { 
                                Text(
                                    text = dateFormat.format(Date(workout.startTimeMillis)),
                                    style = MaterialTheme.typography.labelSmall
                                ) 
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(workout.sport.iconRes),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = viewModel.formatDuration(workout.durationSeconds),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = workout.averageHeartRate.toInt().toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = " BPM",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}