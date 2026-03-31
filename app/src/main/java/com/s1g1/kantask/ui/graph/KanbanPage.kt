package com.s1g1.kantask.ui.graph

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.s1g1.kantask.database.KanbanStatus
import com.s1g1.kantask.database.TaskEntity
import com.s1g1.kantask.viewmodel.TaskState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun KanbanPage(
    snackbarHostState: SnackbarHostState,
    kanbanStatus: KanbanStatus,
    uiState: TaskState,
    scope: CoroutineScope,
    pagerState: PagerState,
    onTaskDropped: (TaskEntity, KanbanStatus) -> Unit
) {
    val currentTasks = uiState.tasks.filter{ it.kanbanStatus.name == kanbanStatus.name }
    val cardShape = RoundedCornerShape(size = 16.dp)
    var isRightColumnActive by remember { mutableStateOf(false) }
    var isLeftColumnActive by remember { mutableStateOf(false) }

    val highlightColor by animateColorAsState(
        targetValue = when{
            isRightColumnActive -> kanbanStatus.getNextColor().copy(alpha=0.5f)
            isLeftColumnActive -> kanbanStatus.getPrevColor().copy(alpha=0.5f)
            else -> Color(0xFFA0A0A0).copy(alpha=0.5f)
        },
        label = "ColumnHighlight"
    )

    val lineWidth by animateDpAsState(
        targetValue = if (isRightColumnActive || isLeftColumnActive) 16.dp else 0.dp,
        animationSpec = if(isRightColumnActive || isLeftColumnActive){
            tween(durationMillis = 800,easing = LinearOutSlowInEasing)
        } else {
            tween(durationMillis = 200,easing = FastOutLinearInEasing)
        }
    )

    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = alpha.value)
            .clip(cardShape)
            .background(
                color=highlightColor
            )
        ,
    ){
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = kanbanStatus.name,
                fontSize = 25.sp,
                fontWeight = FontWeight.Black,
                color = kanbanStatus.color,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                )
        ){
            if(currentTasks.isNotEmpty()){
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(cardShape)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(size = 16.dp)
                        )
                        .drawBehind{
                            if(lineWidth>0.dp){
                                if(isRightColumnActive && pagerState.canScrollForward){
                                    drawLine(
                                        color = Color.Magenta,
                                        start = Offset(size.width-10f, 0f),
                                        end = Offset(size.width-10f, size.height),
                                        strokeWidth = lineWidth.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                } else if (isLeftColumnActive && pagerState.canScrollBackward){
                                    drawLine(
                                        color = Color.Magenta,
                                        start = Offset(10f, 0f),
                                        end = Offset(10f, size.height),
                                        strokeWidth = lineWidth.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                }
                            }
                        }
                        .padding(8.dp)
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(currentTasks, key = { it.id }){ task ->
                        KanbanTaskRow(
                            task = task,
                            onRowClick = { msg ->
                                if (msg.isNotBlank()) {
                                    scope.launch { snackbarHostState.showSnackbar(msg) }
                                }
                            },
                            onTaskDropped = { taskEnt, newKanbanStatus ->
                                onTaskDropped(taskEnt, newKanbanStatus)
                                isRightColumnActive=false
                                isLeftColumnActive=false
                            },
                            onHighlightLeft = {bool -> isLeftColumnActive=bool},
                            onHighlightRight = {bool -> isRightColumnActive=bool},
                        )
                    }
                }
            } else {
                Box(contentAlignment = Alignment.Center,modifier = Modifier.fillMaxSize()
                ) {Text(text="NO TASK", fontWeight = FontWeight.Black)}
            }
        }
    }
}