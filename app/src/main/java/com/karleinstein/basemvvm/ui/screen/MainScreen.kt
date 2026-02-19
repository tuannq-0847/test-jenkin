package com.karleinstein.basemvvm.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.karleinstein.basemvvm.constant.HomeDestination
import com.karleinstein.basemvvm.ui.screen.main.HomeScreen
import com.karleinstein.basemvvm.ui.screen.main.HomeScreenRoute
import com.karleinstein.basemvvm.viewmodel.TodoHomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreenContent(modifier: Modifier = Modifier, openAddTaskScreen: () -> Unit) {
    val navController = rememberNavController()
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        topBar = {},
        bottomBar = {
            val destinations =
                listOf(HomeDestination.Home, HomeDestination.AddTask, HomeDestination.Calendar)
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                destinations.forEach { destination ->
                    val selected = destination.route == currentRoute
                    NavigationBarItem(selected = selected, onClick = {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }, icon = {
                        Icon(
                            painter = painterResource(id = if (selected) destination.iconSelected else destination.icon),
                            contentDescription = destination.label
                        )
                    }, label = { Text(destination.label) })
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeDestination.Home.route,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(HomeDestination.Home.route) {
                val homeViewModel: TodoHomeViewModel = koinViewModel()
                HomeScreenRoute(
                    homeViewModel = homeViewModel
                ) {
                    navController.navigate(HomeDestination.AddTask.route)
                }
            }
            composable(HomeDestination.AddTask.route) {
                LaunchedEffect(Unit) {
                    openAddTaskScreen()
                    // Immediately navigate back to the home route in the inner nav graph
                    // to prevent a navigation loop when the user returns to this screen.
                    navController.navigate(HomeDestination.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                    }
                }
            }
            composable(HomeDestination.Calendar.route) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text("Calendar Screen")
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, openAddTaskScreen: () -> Unit) {
    MainScreenContent(modifier = modifier, openAddTaskScreen)
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreenContent(openAddTaskScreen = {})
}
