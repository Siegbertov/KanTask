package com.s1g1.kantask.ui

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.s1g1.kantask.ui.components.FloatingActionButtons
import com.s1g1.kantask.ui.components.KanTaskTopBar
import com.s1g1.kantask.ui.components.SnackBarHandler
import com.s1g1.kantask.ui.graph.DayScreen
import com.s1g1.kantask.ui.graph.KanbanScreen
import com.s1g1.kantask.viewmodel.task.TaskEvent
import com.s1g1.kantask.viewmodel.task.TaskViewModel
import java.time.LocalDate

object Routes {
    const val DAYSCREEN = "day_screen"
    const val KANBANSCREEN = "kanban_screen"
}

@Composable
fun MainNavGraph(
    tvm: TaskViewModel,
    isDarkTheme: Boolean,
    onToggleThemeChange: ()->Unit
){
    val uiState by tvm.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        topBar={ KanTaskTopBar(
            navController = navController,
            currentRoute = currentRoute,
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
            FloatingActionButtons(
                onAddTaskClick = {tvm.onEvent(TaskEvent.ToggleAddTaskDialog)},
                currentRoute = currentRoute
            )
        }
    ){ innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DAYSCREEN,
            builder = {

                composable(route = Routes.DAYSCREEN){
                    DayScreen(
                        innerPadding = innerPadding,
                        tvm = tvm,
                        uiTaskState=uiState
                    )
                }

                composable(route = Routes.KANBANSCREEN){
                    KanbanScreen(
                        innerPadding = innerPadding,
                        tvm = tvm,
                        uiTaskState=uiState,
                        snackbarHostState = snackbarHostState
                        )
                }
            }
        )
    }
}



