package com.example.budgetmanager.ui.screen.summary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.budgetmanager.ui.screen.main.TopBarEvent

@Composable
fun SummaryScreenDestination(
    onTopBarEvent: (event: TopBarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onTopBarEvent(TopBarEvent.TitleChanged("Summary"))
    }

    SummaryScreen(modifier)
}

@Composable
fun SummaryScreen(modifier: Modifier) {

}