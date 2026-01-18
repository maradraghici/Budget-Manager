package com.example.budgetmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgetmanager.ui.screen.home.HomeScreenDestination
import com.example.budgetmanager.ui.screen.login.LoginScreenDestination
import com.example.budgetmanager.ui.screen.main.MainScreenDestination
import com.example.budgetmanager.ui.screen.signup.SignUpScreenDestination
import com.example.budgetmanager.ui.screen.splash.SplashScreenDestination

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    startDestination: String = Routes.SPLASH
) {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Routes.SPLASH) {
            SplashScreenDestination(
                onNavigate = { decidedRoute ->
                    navController.navigate(decidedRoute) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                modifier = modifier
            )
        }

        composable(Routes.LOGIN) {
            LoginScreenDestination(
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN_APP) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(Routes.SIGNUP) {
                        launchSingleTop = true
                    }
                },
                modifier = modifier
            )
        }

        composable(Routes.SIGNUP) {
            SignUpScreenDestination(
                onSignUpSuccess = {
                    navController.navigate(Routes.MAIN_APP) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack(Routes.LOGIN, inclusive = false)
                },
                modifier = modifier
            )
        }

        composable(Routes.MAIN_APP) {
            MainScreenDestination(
                navigateToAuth = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}