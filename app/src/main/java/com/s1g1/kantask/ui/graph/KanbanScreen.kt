package com.s1g1.kantask.ui.graph

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.s1g1.kantask.database.KanbanStatus
import com.s1g1.kantask.viewmodel.TaskEvent
import com.s1g1.kantask.viewmodel.TaskState
import com.s1g1.kantask.viewmodel.TaskViewModel

@Composable
fun KanbanScreen(
    innerPadding: PaddingValues,
    tvm: TaskViewModel,
    uiState: TaskState,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 1) { KanbanStatus.entries.size }
    HorizontalPager(
        state = pagerState,
        verticalAlignment = Alignment.Top,
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
        pageSpacing = 16.dp,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ){ pagerIndex ->
        KanbanPage(
            snackbarHostState=snackbarHostState,
            kanbanStatus = KanbanStatus.entries[pagerIndex],
            uiState = uiState,
            scope = scope,
            pagerState=pagerState,
            onTaskDropped = { taskEnt, newKanbanStatus ->
                tvm.onEvent(TaskEvent.UpdateTaskKanbanStatus(taskEnt, newKanbanStatus))
            }
        )
    }
}

