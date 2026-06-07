package dev.emanueldias.fitcollectsmartwatch.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Text
import dev.emanueldias.fitcollectsmartwatch.presentation.theme.FitCollectSmartwatchTheme

@Composable
fun MainScreen() {
    AppScaffold() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Tipo de coletas")
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    FitCollectSmartwatchTheme {
        MainScreen()
    }
}