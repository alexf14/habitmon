package com.habitmon.wear

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.habitmon.wear.ui.screen.activity.ActivityScreen
import com.habitmon.wear.ui.screen.debug.DebugScreen
import com.habitmon.wear.ui.screen.home.HomeScreen
import com.habitmon.wear.ui.screen.intro.IntroScreen
import com.habitmon.wear.ui.screen.pokedex.PokedexDetailScreen
import com.habitmon.wear.ui.screen.pokedex.PokedexScreen
import com.habitmon.wear.ui.screen.selection.SelectionScreen
import com.habitmon.wear.ui.screen.welcome.WelcomeScreen
import com.habitmon.wear.ui.theme.HabitMonTheme

object Routes {
    const val WELCOME = "welcome"
    const val SELECTION = "selection"
    const val INTRO = "intro"
    const val HOME = "home"
    const val POKEDEX = "pokedex"
    const val POKEDEX_DETAIL = "pokedex/{speciesId}"
    const val ACTIVITY = "activity"
    const val DEBUG = "debug"

    fun pokedexDetail(speciesId: Int) = "pokedex/$speciesId"
}

@Composable
fun HabitMonApp() {
    HabitMonTheme {
        val navController = rememberSwipeDismissableNavController()

        SwipeDismissableNavHost(
            navController = navController,
            startDestination = Routes.WELCOME
        ) {
            composable(Routes.WELCOME) {
                WelcomeScreen(
                    onStart = {
                        navController.navigate(Routes.SELECTION) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    },
                    onResumeIntro = {
                        navController.navigate(Routes.INTRO) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    },
                    onResumeHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.SELECTION) {
                SelectionScreen(
                    onSelected = {
                        navController.navigate(Routes.INTRO) {
                            popUpTo(Routes.SELECTION) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.INTRO) {
                IntroScreen(
                    onComplete = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.INTRO) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.HOME) {
                HomeScreen(
                    onNavigateToPokedex = {
                        navController.navigate(Routes.POKEDEX)
                    },
                    onNavigateToActivity = {
                        navController.navigate(Routes.ACTIVITY)
                    },
                    onNavigateToDebug = {
                        navController.navigate(Routes.DEBUG)
                    },
                    onNoCompanion = {
                        navController.navigate(Routes.WELCOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.POKEDEX) {
                PokedexScreen(
                    onSpeciesClick = { speciesId ->
                        navController.navigate(Routes.pokedexDetail(speciesId))
                    }
                )
            }

            composable(
                route = Routes.POKEDEX_DETAIL,
                arguments = listOf(navArgument("speciesId") { type = NavType.IntType })
            ) { backStackEntry ->
                val speciesId = backStackEntry.arguments?.getInt("speciesId") ?: return@composable
                PokedexDetailScreen(
                    speciesId = speciesId,
                    onCompanionChanged = { navController.popBackStack(Routes.HOME, inclusive = false) }
                )
            }

            composable(Routes.ACTIVITY) {
                ActivityScreen()
            }

            composable(Routes.DEBUG) {
                DebugScreen()
            }
        }
    }
}
