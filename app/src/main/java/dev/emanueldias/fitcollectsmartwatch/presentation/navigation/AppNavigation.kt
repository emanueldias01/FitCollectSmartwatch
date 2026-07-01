package dev.emanueldias.fitcollectsmartwatch.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import dev.emanueldias.fitcollectsmartwatch.data.model.Sport
import dev.emanueldias.fitcollectsmartwatch.presentation.initial.InitialScreen
import dev.emanueldias.fitcollectsmartwatch.presentation.main.MainScreen
import dev.emanueldias.fitcollectsmartwatch.presentation.simpleHeart.SimpleHeartScreen
import dev.emanueldias.fitcollectsmartwatch.presentation.sport.SportScreen

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
                    onClickSimpleHealth = { navController.navigate("/simpleHeart") },
                    onNavigateToSport = { sport ->
                        navController.navigate("/sport/${sport.name}")
                    }
                )
            }
        )

        composable(
            route = "/simpleHeart",
            content = {
                SimpleHeartScreen(
                    onClickStop = { navController.popBackStack() }
                )
            }
        )

        composable(
            route = "/sport/{sportName}",
            content = { backStackEntry ->
                val sportName = backStackEntry.arguments?.getString("sportName")
                val sport = Sport.valueOf(sportName ?: Sport.RUNNING.name)
                SportScreen(
                    type = sport,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        )
    }

}
