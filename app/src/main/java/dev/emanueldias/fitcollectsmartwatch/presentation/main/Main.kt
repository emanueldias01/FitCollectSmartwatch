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
import dev.emanueldias.fitcollectsmartwatch.data.model.Sport
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.FitCollectSmartwatchTheme

@Composable
fun MainScreen(
    onClickSimpleHealth: () -> Unit = {},
    onNavigateToSport: (Sport) -> Unit = {}
) {
    val sports = Sport.entries

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
                        if (sport == Sport.HEART_RATE) {
                            onClickSimpleHealth()
                        } else {
                            onNavigateToSport(sport)
                        }
                    },
                    label = { Text(sport.displayName) },
                    icon = {
                        Icon(
                            painter = painterResource(sport.iconRes),
                            contentDescription = sport.displayName
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@WearPreviewDevices
@Composable
private fun MainScreenPreview() {
    FitCollectSmartwatchTheme {
        MainScreen()
    }
}
