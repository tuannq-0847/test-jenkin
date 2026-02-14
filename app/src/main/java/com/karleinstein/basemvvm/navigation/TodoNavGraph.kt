package com.karleinstein.basemvvm.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.karleinstein.basemvvm.constant.TodoDestination
import com.karleinstein.basemvvm.navigation.route.SplashRoute
import com.karleinstein.basemvvm.ui.screen.HomeScreen
import com.karleinstein.basemvvm.viewmodel.TodoHomeViewModel

@Composable
fun TodoNavGraph(
    modifier: Modifier = Modifier,
    startDestination: String = TodoDestination.SPLASH_SCREEN,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(
                animationSpec = tween(300)
            ) { fullWidth -> fullWidth }
        },
        exitTransition = {
            slideOutHorizontally(
                animationSpec = tween(300)
            ) { fullWidth -> -fullWidth }
        }
    )
    {
        composable(
            route = TodoDestination.SPLASH_SCREEN,
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) { navBackStackEntry ->
            SplashRoute {
                navController.navigate(route = TodoDestination.HOME_SCREEN) {
                    popUpTo(TodoDestination.SPLASH_SCREEN) {
                        inclusive = true
                    }
                }
            }
        }
        composable(
            route = TodoDestination.HOME_SCREEN
        ) { navBackStackEntry ->
            val homeViewModel: TodoHomeViewModel = viewModel(
                viewModelStoreOwner = navBackStackEntry
            )
            HomeScreen(
                viewModel = homeViewModel,
                modifier = Modifier
            )
        }
    }
}
