package com.s1g1.kantask.ui.graph

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.s1g1.kantask.viewmodel.TaskEvent
import com.s1g1.kantask.viewmodel.TaskState
import com.s1g1.kantask.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DayPage(
    day: LocalDate,
    tvm: TaskViewModel,
    uiState: TaskState
) {
    val tasksForThisDay = remember(day, uiState.tasks) {
        uiState.tasks.filter { it.day == day }
    }

    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000)
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier=Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = alpha.value)
    ){
        Text(
            text="${day.dayOfWeek}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )

        Text(
            text="(${day.format(DateTimeFormatter.ofPattern("dd MMM"))})",
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic
        )

        LazyColumn(
            modifier = Modifier,
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ){
            items(tasksForThisDay, key={it.id}){ task->
                DayTaskRow(
                    taskEnt = task,
                    onCheckBoxClick = { taskEnt -> tvm.onEvent(TaskEvent.ToggleTaskStatus(taskEntity = taskEnt)) },
                    onEditClick = { taskEnt -> tvm.onEvent(TaskEvent.ShowEditDialog(taskEntity = taskEnt)) },
                    onDeleteClick = { taskEnt -> tvm.onEvent(TaskEvent.DeleteTask(taskEntity = taskEnt)) }
                )
            }
        }
    }
}