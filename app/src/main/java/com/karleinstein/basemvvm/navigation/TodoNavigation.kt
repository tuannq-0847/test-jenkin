package com.karleinstein.basemvvm.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.karleinstein.basemvvm.constant.TodoDestination
import com.karleinstein.basemvvm.ui.screen.HomeScreen
import com.karleinstein.basemvvm.ui.screen.SplashScreen
import com.karleinstein.basemvvm.viewmodel.TodoHomeViewModel

@Composable
fun TodoNavigation(
    modifier: Modifier = Modifier,
    startDestination: String = TodoDestination.HOME_SCREEN,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(
            route = TodoDestination.HOME_SCREEN
        ) { navBackStackEntry ->
            val homeViewModel: TodoHomeViewModel = viewModel(
                viewModelStoreOwner = navBackStackEntry
            )
//            HomeScreen(
//                viewModel = homeViewModel,
//                modifier = Modifier
//            )
            SplashScreen()
        }
    }
}