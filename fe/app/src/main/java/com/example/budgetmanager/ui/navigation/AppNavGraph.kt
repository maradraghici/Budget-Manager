package com.example.budgetmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.budgetmanager.ui.screen.budgetdetails.BudgetDetailsScreenDestination
import com.example.budgetmanager.ui.screen.connections.ConnectionsScreenDestination
import com.example.budgetmanager.ui.screen.home.HomeScreenDestination
import com.example.budgetmanager.ui.screen.main.TopBarEvent
import com.example.budgetmanager.ui.screen.profile.ProfileScreenDestination
import com.example.budgetmanager.ui.screen.settings.SettingsScreenDestination
import com.example.budgetmanager.ui.screen.summary.SummaryScreenDestination

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.HOME,
    onTopBarEvent: (event: TopBarEvent) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.HOME) {
            HomeScreenDestination(
                onBudgetClick = { id ->
                    navController.navigate(Routes.budgetDetails(id))
                },
                modifier = modifier
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreenDestination(
                onConnectionsClick = {
                    navController.navigate(Routes.CONNECTIONS)
                },
                onProfileClick = {
                    navController.navigate(Routes.PROFILE)
                },
                modifier = modifier
            )
        }

        composable(
            route = "${Routes.BUDGET_DETAILS}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) {
            BudgetDetailsScreenDestination(
                onTopBarEvent = onTopBarEvent,
                navigateToSummary = { id ->
                    navController.navigate(Routes.summary(id))
                },
                modifier = modifier
            )
        }

        composable(
            route = "${Routes.SUMMARY}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ){
            SummaryScreenDestination(
                onTopBarEvent = onTopBarEvent,
                modifier = modifier
            )
        }

        composable(Routes.CONNECTIONS) {
            ConnectionsScreenDestination(
                onTopBarEvent = onTopBarEvent,
                modifier = modifier
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreenDestination(modifier = modifier)
        }
    }
}
