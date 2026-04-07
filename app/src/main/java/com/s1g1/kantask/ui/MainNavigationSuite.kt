package com.s1g1.kantask.ui

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.s1g1.kantask.ui.components.FloatingActionButtonsNS
import com.s1g1.kantask.ui.components.KanTaskTopBarNS
import com.s1g1.kantask.ui.components.SnackBarHandler
import com.s1g1.kantask.ui.graph.DayScreen
import com.s1g1.kantask.ui.graph.KanbanScreen
import com.s1g1.kantask.ui.graph.NoteScreen
import com.s1g1.kantask.viewmodel.note.NoteEvent
import com.s1g1.kantask.viewmodel.note.NoteViewModel
import com.s1g1.kantask.viewmodel.task.TaskEvent
import com.s1g1.kantask.viewmodel.task.TaskViewModel
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
    nvm: NoteViewModel,
    isDarkTheme: Boolean,
    onToggleThemeChange: () -> Unit
) {
    val uiTaskState by tvm.state.collectAsStateWithLifecycle()
    val uiNoteState by nvm.state.collectAsStateWithLifecycle()

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
            topBar={
                KanTaskTopBarNS(
                    currentDestination = currentDestination,
                    isDarkTheme = isDarkTheme,
                    onToggleThemeChange = { onToggleThemeChange() },
                    onPostponeClick = { tvm.onEvent(TaskEvent.PostponeUndoneFromPast(today = LocalDate.now())) },
                    onTodayScroll = { tvm.onEvent(TaskEvent.SetToday) },
                    isSelectionMode = uiNoteState.selectedNotes.isNotEmpty(),
                    onDeleteSelected = { nvm.onEvent(NoteEvent.ToggleDeleteSelected) },
                    onUpdatePinStateSelected = { nvm.onEvent(NoteEvent.ToggleUpdatePinStateSelected) },
                    onPaletteClicked = { nvm. onEvent(NoteEvent.TogglePalette) },
            ) },

            snackbarHost = {
                SnackBarHandler(
                    snackbarHostState = snackbarHostState
                )
            },

            floatingActionButton = {
                FloatingActionButtonsNS(
                    currentDestination = currentDestination,
                    isSelectionMode = uiNoteState.selectedNotes.isNotEmpty(),
                    onAddTaskClick = { tvm.onEvent(TaskEvent.ToggleAddTaskDialog) },
                    onAddNoteClick = { nvm.onEvent(NoteEvent.ToggleAddNoteDialog) },
                    onUnSelect = {nvm.onEvent(NoteEvent.UnSelect)},
                )
            }
        ){ innerPadding ->
            when(currentDestination){
                MenuDestination.NOTES -> {
                    NoteScreen(
                        innerPadding = innerPadding,
                        nvm = nvm,
                        uiNoteState = uiNoteState
                    )
                }
                MenuDestination.CALENDAR -> {
                    DayScreen(
                        innerPadding = innerPadding,
                        tvm = tvm,
                        uiTaskState=uiTaskState
                    )
                }
                MenuDestination.KANBAN -> {
                    KanbanScreen(
                        innerPadding = innerPadding,
                        tvm = tvm,
                        uiTaskState=uiTaskState,
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}