package dev.emanueldias.fitcollectsmartwatch.presentation.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import dev.emanueldias.fitcollectsmartwatch.R
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.FitCollectSmartwatchTheme

@Composable
fun MainScreen(
    onClickSimpleHealth: () -> Unit = {}
) {
    val sports = listOf(
        SportItem("Corrida", R.drawable.outline_directions_run_24),
        SportItem("Ciclismo", R.drawable.outline_directions_bike_24),
        SportItem("Natação", R.drawable.outline_pool_24),
        SportItem("Academia", R.drawable.outline_fitness_center_24),
        SportItem("Trilha", R.drawable.outline_mountain_flag_24),
        SportItem("Ginástica", R.drawable.outline_sports_gymnastics_24),
        SportItem("Batimentos", R.drawable.rounded_ecg_heart_24),
    )

    val listState = rememberScalingLazyListState()

    ScreenScaffold(scrollState = listState) {
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                top = 32.dp,
                start = 10.dp,
                end = 10.dp,
                bottom = 40.dp
            )
        ) {
            item {
                ListHeader {
                    Text("Modalidades")
                }
            }
            items(sports) { sport ->
                Button(
                    onClick = {
                        if (sport.name == "Batimentos") {
                            onClickSimpleHealth()
                        }
                    },
                    label = { Text(sport.name) },
                    icon = {
                        Icon(
                            painter = painterResource(sport.iconRes),
                            contentDescription = sport.name
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private data class SportItem(val name: String, val iconRes: Int)

@WearPreviewDevices
@Composable
private fun MainScreenPreview() {
    FitCollectSmartwatchTheme {
        MainScreen()
    }
}
