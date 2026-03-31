package com.s1g1.kantask.ui

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.s1g1.kantask.ui.components.KanTaskTopBar
import com.s1g1.kantask.ui.graph.DayScreen
import com.s1g1.kantask.ui.graph.KanbanScreen

object Routes {
    val DAYSCREEN = "day_screen"
    val KANBANSCREEN = "kanban_screen"
}

@Composable
fun MainNavGraph(

){
    val navController = rememberNavController()
    Scaffold(
        topBar={ KanTaskTopBar(
            navController = navController
        ) }
    ){ innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DAYSCREEN,
            builder = {

                composable(route = Routes.DAYSCREEN){
                    DayScreen(innerPadding = innerPadding)
                }

                composable(route = Routes.KANBANSCREEN){
                    KanbanScreen(innerPadding = innerPadding)
                }
            }
        )
    }
}



