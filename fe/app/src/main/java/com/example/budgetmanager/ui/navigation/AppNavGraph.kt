package com.example.budgetmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgetmanager.ui.screen.budgetdetails.BudgetDetailsScreenDestination
import com.example.budgetmanager.ui.screen.home.HomeScreenDestination
import com.example.budgetmanager.ui.screen.settings.SettingsScreenDestination

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.HOME) {
            HomeScreenDestination(modifier = modifier)
        }

        composable(Routes.SETTINGS) {
            SettingsScreenDestination(modifier = modifier)
        }

        composable(Routes.BUDGET_DETAILS) {
            BudgetDetailsScreenDestination(modifier = modifier)
        }
    }
}
