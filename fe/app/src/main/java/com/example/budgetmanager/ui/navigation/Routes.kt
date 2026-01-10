package com.example.budgetmanager.ui.navigation

object Routes {
    const val LOGIN = "login"
    const val SIGNUP = "signup"

    const val MAIN_APP = "main_app"

    const val HOME = "Home"
    const val SETTINGS = "Settings"

    const val BUDGET_DETAILS = "budget_details"

    fun budgetDetails(id: Long): String = "$BUDGET_DETAILS/$id"
}