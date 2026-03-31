package com.s1g1.kantask.ui.graph

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.s1g1.kantask.viewmodel.TaskEvent
import com.s1g1.kantask.viewmodel.TaskState
import com.s1g1.kantask.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun DayScreen(
    innerPadding: PaddingValues,
    tvm: TaskViewModel,
    uiState: TaskState
) {
    val pagerState = rememberPagerState(
        initialPage = tvm.dayCount,
        pageCount = { tvm.dayCount * 3 }
    )

    LaunchedEffect(pagerState.currentPage) {
        if (!tvm.isProgrammaticScroll) {
            tvm.onEvent(TaskEvent.OnPageChanged(pagerState.currentPage))
        }
    }

    LaunchedEffect(uiState.selectedDate) {
        val daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), uiState.selectedDate)
        val targetPage = tvm.dayCount + daysBetween.toInt()
        if (pagerState.currentPage != targetPage) {
            try {
                tvm.isProgrammaticScroll = true
                pagerState.animateScrollToPage(targetPage)
            } catch (e: Exception) {
                Log.d("DAYSCREENTAG", e.toString())
            } finally {
                tvm.isProgrammaticScroll = false
            }
        }
    }

    Box(modifier = Modifier.padding(innerPadding)){
        HorizontalPager(state = pagerState,modifier = Modifier.fillMaxSize()
        ) { page ->
            Column(
                modifier=Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ){
                val dateForPage = LocalDate.now().plusDays((page - tvm.dayCount).toLong())
                DayPage(
                    day = dateForPage,
                    tvm = tvm,
                    uiState = uiState
                )
            }
        }
    }

    if(uiState.isAddTaskDialogVisible){
        AddNewOrEditTaskDialog(
            taskEnt = null,
            onFinalAction = { taskEnt -> tvm.onEvent(TaskEvent.AddTask(taskEntity = taskEnt)) },
            onDismiss = { tvm.onEvent(TaskEvent.ToggleAddTaskDialog) }
        )
    }
    if(uiState.isEditTaskDialogVisible){
        AddNewOrEditTaskDialog(
            taskEnt = uiState.taskToEdit,
            onFinalAction = { taskEnt -> tvm.onEvent(TaskEvent.UpdateTask(taskEntity = taskEnt)) },
            onDismiss = { tvm.onEvent(TaskEvent.ToggleEditTaskDialog) }
        )
    }
}