package com.s1g1.kantask.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.s1g1.kantask.viewmodel.TaskEvent
import com.s1g1.kantask.viewmodel.TaskViewModel

object Routes {
    const val DAYSCREEN = "day_screen"
    const val KANBANSCREEN = "kanban_screen"
}

@Composable
fun MainNavGraph(
    tvm: TaskViewModel,
){
    val uiState by tvm.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        topBar={ KanTaskTopBar(
            navController = navController,
            currentRoute = currentRoute
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
                        uiState=uiState
                    )
                }

                composable(route = Routes.KANBANSCREEN){
                    KanbanScreen(
                        innerPadding = innerPadding,
                        tvm = tvm,
                        uiState=uiState,
                        snackbarHostState = snackbarHostState
                        )
                }
            }
        )
    }
}



