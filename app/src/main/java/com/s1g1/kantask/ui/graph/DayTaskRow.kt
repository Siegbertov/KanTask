package com.s1g1.kantask.ui.graph

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.s1g1.kantask.R
import com.s1g1.kantask.database.tasks.KanbanStatus
import com.s1g1.kantask.database.tasks.TaskEntity
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalTime
import com.s1g1.kantask.database.tasks.Priority

@Composable
fun DayTaskRow(
    taskEnt: TaskEntity,
    onCheckBoxClick: (TaskEntity) -> Unit,
    onEditClick: (TaskEntity) -> Unit,
    onDeleteClick: (TaskEntity) -> Unit
) {
    val delayMillis = 500
    val currentContext = LocalContext.current
    val hint = stringResource(R.string.hint_delete_task)
    var showHiddenButtons by remember { mutableStateOf(false) }
    val animOffset = remember { Animatable(0f) }
    var isVisible by remember { mutableStateOf(true) }
    val priorityColor by remember(taskEnt.priority) {
        derivedStateOf { Priority.fromInt(taskEnt.priority.count).color }
    }
    val cardShape = RoundedCornerShape(size = 16.dp)

    LaunchedEffect(showHiddenButtons) {
        if (showHiddenButtons) {
            animOffset.animateTo(1f, spring(stiffness = Spring.StiffnessLow))
        } else {animOffset.animateTo(0f)}
    }
    LaunchedEffect(isVisible) {
        if (!isVisible){delay((50 + delayMillis).toLong()); onDeleteClick(taskEnt)}
    }

    AnimatedVisibility(
        visible = isVisible,
        exit = scaleOut(targetScale = 0f,animationSpec = tween(durationMillis = delayMillis))
                + fadeOut(animationSpec = tween(durationMillis = delayMillis))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .heightIn(min = 100.dp)
                .padding(4.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(16.dp)
                )
                .clip(cardShape)
                .combinedClickable(
                    onClick = {if(showHiddenButtons){showHiddenButtons=false}},
                    onLongClick = {if(!showHiddenButtons){showHiddenButtons=true}}
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ){
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .blur(if (showHiddenButtons) 2.dp else 0.dp)
                ){
                    Checkbox(
                        modifier = Modifier
                            .padding(4.dp),
                        checked = taskEnt.isDone,
                        onCheckedChange = {if (!showHiddenButtons) {onCheckBoxClick(taskEnt)}}
                    )
                    TimeDurationComponent(
                        modifier = Modifier
                            .fillMaxHeight(),
                        priorityColor = priorityColor,
                        possibleTime = taskEnt.time,
                        possibleDuration = taskEnt.duration,
                        shouldNotify = taskEnt.shouldNotify,
                    )
                    TaskDataComponent(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = 8.dp, vertical = 16.dp)
                            .weight(1f),
                        priorityColor = priorityColor,
                        title = taskEnt.title,
                        description = taskEnt.description,
                    )
                }
                if(showHiddenButtons){
                    HiddenButtonsComponent(
                        taskEnt = taskEnt,
                        animValue = animOffset.value,
                        onEditClick = {taskEnt -> onEditClick(taskEnt); showHiddenButtons=!showHiddenButtons},
                        onDeleteShortClick = {showDeleteHint(context = currentContext, text = hint)},
                        onDeleteLongClick = {isVisible=false;showHiddenButtons=!showHiddenButtons}
                    )
                } else {
                    KanbanStatusLabelComponent(
                        modifier = Modifier.padding(4.dp).align(Alignment.TopEnd),
                        kanbanStatus = taskEnt.kanbanStatus
                    )
                }
            }
        }
    }
}

@Composable
fun KanbanStatusLabelComponent(
    modifier: Modifier,
    kanbanStatus: KanbanStatus
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = kanbanStatus.color.copy(alpha=0.75f)
    ) {
        Text(
            text = kanbanStatus.name,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}

fun showDeleteHint(context: Context, text: String){
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}

@Composable
fun HiddenButtonsComponent(
    taskEnt: TaskEntity,
    animValue: Float,
    onEditClick: (TaskEntity) -> Unit,
    onDeleteShortClick: () -> Unit,
    onDeleteLongClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ){
        if(!taskEnt.isDone){
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = Color.Yellow,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .offset(x = (50 * (1 - animValue)).dp)
                    .combinedClickable(onClick={
                        onEditClick(taskEnt)
                    })
            )
        }
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .offset(x = if(taskEnt.isDone) 0.dp else (-50 * (1 - animValue)).dp)
                .combinedClickable(
                    onClick = {
                        onDeleteShortClick()
                    },
                    onLongClick = {
                        onDeleteLongClick()
                    }))
    }
}

@Composable
fun TaskDataComponent(
    modifier: Modifier,
    priorityColor: Color,
    title: String,
    description: String?
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ){
        Box(modifier = Modifier.fillMaxWidth()){
            Text(
                text=title,
                fontWeight = FontWeight.Black,
                color = priorityColor,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        description?.let{Text(text=it, fontStyle = FontStyle.Italic)}
    }
}

@Composable
fun TimeDurationComponent(
    modifier: Modifier,
    priorityColor: Color,
    possibleTime: LocalTime?,
    possibleDuration: Duration?,
    shouldNotify: Boolean,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier,
    ) {
        if (possibleTime != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier=Modifier
                    .fillMaxHeight()
                    .drawBehind {
                        val strokeWidth = 2.dp.toPx()
                        val dashPath = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.5f),
                            start = Offset((size.width - strokeWidth) / 2, 0f),
                            end = Offset((size.width - strokeWidth) / 2, size.height),
                            strokeWidth = strokeWidth,
                            pathEffect = dashPath
                        )
                    }
            ){
                if (possibleDuration != null) {
                    Text(
                        text = possibleTime.toString(),
                        color = priorityColor,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                    Spacer(modifier=Modifier.weight(1.0f))
                    if(shouldNotify){
                        Icon(
                            imageVector = Icons.Default.AlarmOn,
                            contentDescription = null,
                            tint = priorityColor,
                            modifier = Modifier.scale(0.75f)
                        )
                        Spacer(modifier=Modifier.weight(1.0f))
                    }
                    Text(
                        text = possibleTime.plusMinutes(possibleDuration.toMinutes()).toString(),
                        color = priorityColor,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                } else {
                    Text(text = possibleTime.toString(), color = priorityColor, fontWeight = FontWeight.Black)
                    if(shouldNotify){
                        Icon(
                            imageVector = Icons.Default.AlarmOn,
                            contentDescription = null,
                            tint = priorityColor,
                            modifier = Modifier.scale(0.75f)
                        )
                    }
                }
            }
        } else {
            if (possibleDuration != null) {
                Text(
                    text = "~${possibleDuration.toMinutes()}m~",
                    fontStyle = FontStyle.Italic,
                    color = priorityColor,
                )
            }
        }
    }
}