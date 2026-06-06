package dev.emanueldias.fitcollectsmartwatch.presentation.initial

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import dev.emanueldias.fitcollectsmartwatch.R
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.AndroidGreenDark
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.FitCollectSmartwatchTheme

@Composable
fun InitialScreen(onNavigateToScan: () -> Unit) {
    ScreenScaffold() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.outline_fitness_center_24),
                contentDescription = "logo",
                modifier = Modifier.size(60.dp)
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(text = "FitCollect", color = AndroidGreenDark, fontSize = 16.sp)

            Spacer(modifier = Modifier.size(16.dp))

            Button(
                onClick = onNavigateToScan,
            ) {
                Icon(painter = painterResource(R.drawable.outline_play_arrow_24), contentDescription = "init scan")
            }

        }
    }
}


@WearPreviewFontScales
@WearPreviewDevices
@Composable
private fun InitialScreenPreview() {
    FitCollectSmartwatchTheme {
        InitialScreen(onNavigateToScan = {})
    }
}