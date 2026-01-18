package com.example.budgetmanager.ui.screen.main

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.budgetmanager.ui.navigation.AppNavGraph
import com.example.budgetmanager.ui.navigation.Routes

@Composable
fun MainScreenDestination(navigateToAuth: () -> Unit) {
    val vm: MainViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.effect.collect {
            when(it) {
                MainEffect.OnSignOutClick -> {
                    navigateToAuth()
                }
            }
        }
    }

    MainScreen(state, vm::onEvent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(state: MainState, onEvent: (event: MainEvent) -> Unit) {
    val appNavController = rememberNavController()
    val navBackStackEntry by appNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        topBar = {
            if (currentDestination?.route !in bottomNavItems.keys) {
                TopAppBar(
                    title = {
                        Text(
                            text = state.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { appNavController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBackIosNew,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                )
            }
        },

        bottomBar = {
            if (currentDestination?.route in bottomNavItems.keys) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    bottomNavItems.forEach { (route, icon) ->
                        val isSelected =
                            currentDestination?.hierarchy?.any { it.route == route } == true
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                appNavController.navigate(route) {
                                    popUpTo(appNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Row(
                                    modifier = Modifier.padding(vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = route,
                                            modifier = Modifier.size(30.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = route,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    } else {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = route,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                            },
                            label = null,
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                unselectedIconColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        AppNavGraph(
            navController = appNavController,
            onEvent = onEvent,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

private val bottomNavItems = mapOf(
    Routes.HOME to Icons.Outlined.Home,
    Routes.SETTINGS to Icons.Outlined.Settings
)