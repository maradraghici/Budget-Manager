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
import com.example.budgetmanager.ui.screen.home.HomeScreenDestination
import com.example.budgetmanager.ui.screen.main.MainEvent
import com.example.budgetmanager.ui.screen.profile.ProfileScreenDestination
import com.example.budgetmanager.ui.screen.summary.SummaryScreenDestination

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.HOME,
    onEvent: (event: MainEvent) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.HOME) {
            HomeScreenDestination(
                onBudgetClick = { id, title ->
                    navController.navigate(Routes.budgetDetails(id, title))
                },
                modifier = modifier
            )
        }

        composable(Routes.SETTINGS) {
            ProfileScreenDestination(
                navigateToAuth = { onEvent(MainEvent.OnSignOutClick) },
                modifier = modifier
            )
        }

        composable(
            route = "${Routes.BUDGET_DETAILS}/{id}/{title}",
            arguments = listOf(
                navArgument("id") { type = NavType.LongType },
                navArgument("title") { type = NavType.StringType }
            )
        ) {
            BudgetDetailsScreenDestination(
                onTitleChanged = { onEvent(MainEvent.OnTitleChanged(it)) },
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
                onTitleChanged = { onEvent(MainEvent.OnTitleChanged(it)) },
                modifier = modifier
            )
        }
    }
}
