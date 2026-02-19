package com.karleinstein.basemvvm.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.karleinstein.basemvvm.constant.TodoDestination
import com.karleinstein.basemvvm.navigation.route.SplashRoute
import com.karleinstein.basemvvm.ui.screen.MainScreen
import com.karleinstein.basemvvm.ui.screen.main.AddTaskRoute
import com.karleinstein.basemvvm.viewmodel.TodoHomeViewModel
import org.koin.androidx.compose.koinViewModel

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
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    )
    {
        composable(
            route = TodoDestination.SPLASH_SCREEN,
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            }
        ) { navBackStackEntry ->
            SplashRoute {
                navController.navigate(TodoDestination.HOME_SCREEN) {
                    popUpTo(TodoDestination.SPLASH_SCREEN) {
                        inclusive = true
                    }
                }
            }
        }
        composable(
            route = TodoDestination.HOME_SCREEN
        ) { navBackStackEntry ->
            MainScreen(
                modifier = Modifier
            ) {
                navController.navigate(TodoDestination.ADD_NEW_TASK)
            }
        }

        composable(
            route = TodoDestination.ADD_NEW_TASK
        ) { navBackStackEntry ->
            val homeViewModel: TodoHomeViewModel = koinViewModel()
            AddTaskRoute(
                homeViewModel
            ) {
                navController.popBackStack()
            }
        }
    }
}
