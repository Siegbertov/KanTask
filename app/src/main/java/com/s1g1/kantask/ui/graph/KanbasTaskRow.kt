package com.s1g1.kantask.ui.graph

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.s1g1.kantask.database.KanbanStatus
import com.s1g1.kantask.database.Priority
import com.s1g1.kantask.database.TaskEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun KanbasTaskRow(
    task: TaskEntity,
    onRowClick: (String) -> Unit,
    onTaskDropped: (TaskEntity, KanbanStatus) -> Unit,
    onHighlightLeft: (Boolean) -> Unit,
    onHighlightRight: (Boolean) -> Unit
) {
    val cardShape = RoundedCornerShape(size = 16.dp)
    val currentColor = Priority.fromInt(task.priority.count).color
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(LocalDensity.current){configuration.screenWidthDp.dp.toPx()}
    val pageVisibleWidth = screenWidthPx - with(LocalDensity.current) { 64.dp.toPx() }
    val threshold = pageVisibleWidth * 0.2f
    var offsetX by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 4000)
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .graphicsLayer(alpha = alpha.value)
            .zIndex(if (isDragging) 1f else 0f)
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .fillMaxWidth()
            .padding(4.dp)
            .clip(cardShape)
            .clickable(onClick = { onRowClick(task.description?:"") } )
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false
                        val newStatus = if(offsetX > threshold) {
                            when (task.kanbanStatus) {
                                KanbanStatus.Todo -> KanbanStatus.InProgress
                                KanbanStatus.InProgress -> KanbanStatus.Done
                                else -> task.kanbanStatus
                            }
                        } else if (offsetX < -threshold){
                            when (task.kanbanStatus) {
                                KanbanStatus.Done -> KanbanStatus.InProgress
                                KanbanStatus.InProgress -> KanbanStatus.Todo
                                else -> task.kanbanStatus
                            }
                        } else {task.kanbanStatus}
                        if (newStatus != task.kanbanStatus) {
                            onTaskDropped(task, newStatus)
                        }
                        onHighlightRight(false)
                        onHighlightLeft(false)
                        offsetX = 0f
                    },
                    onDragCancel = {
                        isDragging = false
                        onHighlightRight(false)
                        onHighlightLeft(false)
                        offsetX = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        onHighlightRight(offsetX > threshold)
                        onHighlightLeft(offsetX < -threshold)
                    }
                )
            }
            .background(if (isDragging) currentColor.copy(alpha = 0.25f) else currentColor.copy(alpha = 0.1f))
            .border(width = 2.dp,color = MaterialTheme.colorScheme.outline,shape = cardShape)
            .padding(20.dp)
    ){
        Text(
            text=task.title,
            fontSize = 14.sp,
            color= currentColor,
            fontWeight = FontWeight.Black
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text=when(ChronoUnit.DAYS.between(LocalDate.now(), task.day).toInt()){
                    -1 -> "YST"
                    0 -> "TDY"
                    1 -> "TMW"
                    else -> task.day.format(DateTimeFormatter.ofPattern("dd MMM"))
                }
            )
            task.duration?.let{
                Text(
                    text="~${it.toMinutes()}m~",
                    fontStyle = FontStyle.Italic,
                )
            }
        }
    }
}