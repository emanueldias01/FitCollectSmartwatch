package dev.emanueldias.fitcollectsmartwatch.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import dev.emanueldias.fitcollectsmartwatch.R
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.FitCollectSmartwatchTheme

@Composable
fun MainScreen(
    onClickSimpleHealth: () -> Unit = {}
) {
    AppScaffold() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onClickSimpleHealth
            ) {
                Icon(
                    painter = painterResource(R.drawable.rounded_ecg_heart_24),
                    contentDescription = "logo"
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