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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kaamkram.app.ui.customers.CreateCustomerScreen
import com.kaamkram.app.ui.customers.CustomersScreen
import com.kaamkram.app.ui.estimates.CreateEstimateScreen
import com.kaamkram.app.ui.invoices.CreateInvoiceScreen
import com.kaamkram.app.ui.jobs.CreateJobScreen
import com.kaamkram.app.ui.jobs.JobDetailScreen
import com.kaamkram.app.ui.jobs.JobsScreen
import com.kaamkram.app.ui.money.MoneyScreen
import com.kaamkram.app.ui.profile.BusinessProfileScreen
import com.kaamkram.app.ui.settings.SettingsScreen
import com.kaamkram.app.ui.today.TodayScreen

@Composable
fun KaamKramAppContent(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = Screen.bottomNavScreens.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Main Bottom Bar Screens
            composable(Screen.Today.route) {
                TodayScreen(
                    onNavigateToNewJob = { navController.navigate(Screen.NewJob.route) },
                    onNavigateToJobDetail = { jobId -> navController.navigate(Screen.JobDetail.createRoute(jobId)) }
                )
            }
            composable(Screen.Jobs.route) {
                JobsScreen(
                    onNavigateToNewJob = { navController.navigate(Screen.NewJob.route) },
                    onNavigateToJobDetail = { jobId -> navController.navigate(Screen.JobDetail.createRoute(jobId)) }
                )
            }
            composable(Screen.Customers.route) {
                CustomersScreen(
                    onNavigateToNewCustomer = { navController.navigate(Screen.NewCustomer.route) },
                    onNavigateToCustomerDetail = { customerId -> }
                )
            }
            composable(Screen.Money.route) {
                MoneyScreen()
            }
            composable(Screen.More.route) {
                SettingsScreen(
                    onNavigateToProfile = { navController.navigate(Screen.BusinessProfile.route) }
                )
            }

            // Sub-screens
            composable(Screen.NewJob.route) {
                CreateJobScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onJobCreated = { jobId ->
                        navController.navigate(Screen.JobDetail.createRoute(jobId)) {
                            popUpTo(Screen.Jobs.route)
                        }
                    }
                )
            }
            composable(
                route = Screen.JobDetail.route,
                arguments = listOf(navArgument("jobId") { type = NavType.StringType })
            ) { backStackEntry ->
                val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
                JobDetailScreen(
                    jobId = jobId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCreateEstimate = { id ->
                        navController.navigate(Screen.EstimateDetail.createRoute(id))
                    },
                    onNavigateToCreateInvoice = { id ->
                        navController.navigate(Screen.InvoiceDetail.createRoute(id))
                    }
                )
            }
            composable(Screen.NewCustomer.route) {
                CreateCustomerScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onCustomerCreated = { customerId ->
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = Screen.EstimateDetail.route,
                arguments = listOf(navArgument("jobId") { type = NavType.StringType })
            ) { backStackEntry ->
                val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
                CreateEstimateScreen(
                    jobId = jobId,
                    onNavigateBack = { navController.popBackStack() },
                    onEstimateSaved = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.InvoiceDetail.route,
                arguments = listOf(navArgument("jobId") { type = NavType.StringType })
            ) { backStackEntry ->
                val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
                CreateInvoiceScreen(
                    jobId = jobId,
                    onNavigateBack = { navController.popBackStack() },
                    onInvoiceCreated = { navController.popBackStack() }
                )
            }
            composable(Screen.BusinessProfile.route) {
                BusinessProfileScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
