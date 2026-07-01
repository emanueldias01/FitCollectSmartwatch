package dev.emanueldias.fitcollectsmartwatch.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import dev.emanueldias.fitcollectsmartwatch.presentation.initial.InitialScreen
import dev.emanueldias.fitcollectsmartwatch.presentation.main.MainScreen
import dev.emanueldias.fitcollectsmartwatch.presentation.simpleHeart.SimpleHeartScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberSwipeDismissableNavController()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = "/initial"
    ) {
        composable(
            route = "/initial",
            content = {
                InitialScreen(
                    onNavigateToMain = { navController.navigate("/main") }
                )
            }
        )

        composable(
            route = "/main",
            content = {
                MainScreen(
                    onClickSimpleHealth = { navController.navigate("/simpleHeart") }
                )
            }
        )

        composable(
            route = "/simpleHeart",
            content = {
                SimpleHeartScreen(
                    onClickStop = {navController.popBackStack()}
                )
            }
        )
    }

}

