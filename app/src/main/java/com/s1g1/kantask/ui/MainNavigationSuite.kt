package com.s1g1.kantask.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.s1g1.kantask.ui.components.FloatingActionButtonsNS
import com.s1g1.kantask.ui.components.KanTaskTopBarNS
import com.s1g1.kantask.ui.components.SnackBarHandler
import com.s1g1.kantask.ui.graph.DayScreen
import com.s1g1.kantask.ui.graph.KanbanScreen
import com.s1g1.kantask.viewmodel.TaskEvent
import com.s1g1.kantask.viewmodel.TaskViewModel
import java.time.LocalDate


enum class MenuDestination(
    val label: String,
    val icon: ImageVector,
) {
    NOTES("Notes", Icons.Default.Book),
    CALENDAR("Calendar", Icons.Default.CalendarMonth),
    KANBAN("Kanban", Icons.Default.ViewKanban),
}

@Composable
fun MainNavigationSuite(
    tvm: TaskViewModel,
    isDarkTheme: Boolean,
    onToggleThemeChange: () -> Unit
) {
    val uiState by tvm.state.collectAsStateWithLifecycle()
    var currentDestination by rememberSaveable { mutableStateOf(MenuDestination.CALENDAR) }
    val snackbarHostState = remember { SnackbarHostState() }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            MenuDestination.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    )
    {
        Scaffold(
            topBar={ KanTaskTopBarNS(
                currentDestination=currentDestination,
                isDarkTheme=isDarkTheme,
                onToggleThemeChange={onToggleThemeChange()},
                onPostponeClick = {tvm.onEvent(TaskEvent.PostponeUndoneFromPast(today = LocalDate.now()))},
                onTodayScroll = {tvm.onEvent(TaskEvent.SetToday)}
            ) },

            snackbarHost = {
                SnackBarHandler(
                    snackbarHostState = snackbarHostState
                )
            },

            floatingActionButton = {
                FloatingActionButtonsNS(
                    onAddTaskClick = { tvm.onEvent(TaskEvent.ToggleAddTaskDialog) },
                    currentDestination = currentDestination,
                )
            }
        ){ innerPadding ->
            when(currentDestination){
                MenuDestination.NOTES -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){
                        Text(currentDestination.label)
                    }
                }
                MenuDestination.CALENDAR -> {
                    DayScreen(
                        innerPadding = innerPadding,
                        tvm = tvm,
                        uiState=uiState
                    )
                }
                MenuDestination.KANBAN -> {
                    KanbanScreen(
                        innerPadding = innerPadding,
                        tvm = tvm,
                        uiState=uiState,
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}