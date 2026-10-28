package com.kaamkram.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kaamkram.app.ui.customers.CustomersScreen
import com.kaamkram.app.ui.jobs.JobsScreen
import com.kaamkram.app.ui.money.MoneyScreen
import com.kaamkram.app.ui.settings.SettingsScreen
import com.kaamkram.app.ui.today.TodayScreen

@Composable
fun KaamKramAppContent(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                Screen.bottomNavScreens.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = {
                            screen.icon?.let { icon ->
                                Icon(icon, contentDescription = stringResource(screen.titleRes))
                            }
                        },
                        label = { Text(stringResource(screen.titleRes)) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Today.route) {
                TodayScreen(
                    onNavigateToNewJob = { navController.navigate(Screen.Jobs.route) },
                    onNavigateToJobDetail = { jobId -> }
                )
            }
            composable(Screen.Jobs.route) {
                JobsScreen(
                    onNavigateToNewJob = {},
                    onNavigateToJobDetail = { jobId -> }
                )
            }
            composable(Screen.Customers.route) {
                CustomersScreen(
                    onNavigateToNewCustomer = {},
                    onNavigateToCustomerDetail = { customerId -> }
                )
            }
            composable(Screen.Money.route) {
                MoneyScreen()
            }
            composable(Screen.More.route) {
                SettingsScreen(
                    onNavigateToProfile = {}
                )
            }
        }
    }
}
