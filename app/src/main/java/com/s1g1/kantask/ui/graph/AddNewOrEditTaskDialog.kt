package com.s1g1.kantask.ui.graph

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.s1g1.kantask.R
import com.s1g1.kantask.database.KanbanStatus
import com.s1g1.kantask.database.TaskEntity
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.text.ifEmpty

data class TaskFormState(
    val titleMaxLength: Int = 20,
    val title: String = "",
    val description: String = "",
    val duration: String = "",

    val dateShow: Boolean = false,
    val dateMillis: Long = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),

    val timeShow: Boolean = false,
    val timePicked: Boolean = false,
    val timeH: Int = 0,
    val timeM: Int = 0,

    val kanbanStatus: KanbanStatus = KanbanStatus.Todo
){
    fun fromTask(taskEntity: TaskEntity) : TaskFormState{
        return this.copy(
            title = taskEntity.title,
            description = taskEntity.description ?: "",
            duration = taskEntity.duration?.toMinutes()?.toString() ?: "",
            dateMillis = taskEntity.day.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            timeH = taskEntity.time?.hour ?: 0,
            timeM = taskEntity.time?.minute ?: 0,
            timePicked = taskEntity.time!=null,

            kanbanStatus = taskEntity.kanbanStatus
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewOrEditTaskDialog(
    taskEnt: TaskEntity?,
    onFinalAction:(TaskEntity)->Unit,
    onDismiss:()->Unit
){
    var formState by remember {
        val initial = TaskFormState()
        mutableStateOf(if (taskEnt != null) initial.fromTask(taskEnt) else initial)
    }

    val timePickerState = rememberTimePickerState(
        initialHour = formState.timeH,
        initialMinute = formState.timeM,
        is24Hour = true
    )

    val scrollState = rememberScrollState()

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {Text(text = if(taskEnt==null){
                stringResource(R.string.title_add)
            }else{stringResource(R.string.title_edit)}) }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier=Modifier.fillMaxWidth().verticalScroll(scrollState)
            ){
                DatePickerComponent(
                    modifier = Modifier,
                    dateInMillis = formState.dateMillis,
                    onDateIconClick = {formState = formState.copy(dateShow = true)},
                )

                TaskTitleField(
                    modifier = if(taskEnt != null){Modifier} else {Modifier.focusRequester(focusRequester)},
                    title = formState.title,
                    maxLength=formState.titleMaxLength,
                    onChanged = {if(it.length <= formState.titleMaxLength){formState = formState.copy(title=it)}},
                )

                TaskDescriptionField(
                    modifier = if(taskEnt != null){Modifier.focusRequester(focusRequester)} else {Modifier},
                    description = formState.description,
                    onChanged = {formState = formState.copy(description=it)})

                TaskDurationField(
                    modifier = Modifier.fillMaxWidth(),
                    duration = formState.duration,
                    onChanged = {formState = formState.copy(duration=it)}
                )

                TimePickerComponent(
                    modifier = Modifier.fillMaxWidth(),
                    timePickerState = timePickerState,
                    isTimePicked = formState.timePicked,
                    onTogglePickedTime = {
                        formState = formState.copy(timePicked = !formState.timePicked)
                        timePickerState.hour = 0
                        timePickerState.minute = 0
                    },
                )

                if (formState.dateShow){
                    TaskDatePicker(
                        selectedDateMillis = formState.dateMillis,
                        onDateSelected = {formState = formState.copy(dateMillis = it)},
                        onDismiss = {formState = formState.copy(dateShow=false)
                        }
                    )
                }
            }
        },
        confirmButton = {
            OutlinedButton(onClick = {
                if (formState.title.isNotBlank()){
                    onFinalAction(

                        TaskEntity(
                            id = if(taskEnt!=null){taskEnt.id}else{0},
                            title = formState.title,
                            description = formState.description.ifEmpty { null },
                            day = fromMillisToLocalDate(formState.dateMillis),
                            time = if(formState.timePicked){LocalTime.of(timePickerState.hour, timePickerState.minute)}else{null},
                            duration = if(formState.duration.isNotEmpty() && formState.duration.all { it.isDigit() }){Duration.ofMinutes(formState.duration.toLong())}else{null},
                            kanbanStatus = formState.kanbanStatus
                        )
                    )
                    onDismiss()
                }
            }) {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
                    Text(text= if(taskEnt==null){ stringResource(R.string.button_text_add) }else{stringResource(R.string.button_text_edit)})
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerComponent(
    modifier: Modifier = Modifier,
    timePickerState: TimePickerState,
    isTimePicked: Boolean,
    onTogglePickedTime:()->Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ){
        Column(
            modifier=Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Row(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text="Set Time:",
                    fontWeight = FontWeight.Black,
                    modifier=Modifier
                        .padding(horizontal = 10.dp)
                )
                Switch(
                    checked = isTimePicked,
                    onCheckedChange = { onTogglePickedTime() },
                    thumbContent = {
                        if(isTimePicked){
                            Icon(imageVector = Icons.Default.AlarmOn, contentDescription = null)
                        }else{
                            Icon(imageVector = Icons.Default.AlarmOff,contentDescription = null)
                        }
                    }
                )
            }
            Row(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text="Should Notify:",
                    fontWeight = FontWeight.Black,
                    modifier=Modifier
                        .padding(horizontal = 10.dp)
                )
            }
            TimeInput(
                state = timePickerState,
                modifier = Modifier
                    .padding(16.dp)
                    .scale(0.8f)
                    .padding(0.dp)
            )
        }
    }
}

@Composable
fun DatePickerComponent(
    modifier:Modifier = Modifier,
    dateInMillis: Long,
    onDateIconClick:()->Unit,
) {
    Row(
        modifier=modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text=fromMillisToLocalDate(dateInMillis).format(DateTimeFormatter.ofPattern("dd MMMM y")),
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick={onDateIconClick()}) { Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)}
    }
}


@Composable
fun TaskDurationField(
    modifier: Modifier = Modifier,
    duration: String,
    onChanged: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier){
        OutlinedTextField(
            value = duration,
            onValueChange = {onChanged(it)},
            label = { Text(stringResource(R.string.duration_field_label))},
            placeholder = {Text(text=stringResource(R.string.duration_field_placeholder), fontStyle = FontStyle.Italic)},
            leadingIcon = { Icon(imageVector = Icons.Default.Timer, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                if(duration.isNotEmpty()){
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        Modifier.combinedClickable(
                            onClick={},
                            onLongClick={onChanged("")}
                        )
                    )
                }
            },
            isError = (duration.isNotBlank() && (duration.toIntOrNull()==null || duration.contains("-"))),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TaskDescriptionField(
    modifier: Modifier,
    description: String,
    onChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = description,
        modifier = modifier,
        leadingIcon = { Icon(imageVector = Icons.Default.Description, contentDescription = null) },
        trailingIcon = {
            if(description.isNotEmpty())
            {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    Modifier.combinedClickable(
                        onClick={},
                        onLongClick={onChanged("")}
                    )
                )
            }
        },
        onValueChange = {onChanged(it)},
        label = { Text(stringResource(R.string.description_field_label))},
        placeholder = { Text(stringResource(R.string.description_field_placeholder), fontStyle = FontStyle.Italic)},
    )
}

@Composable
fun TaskTitleField(
    modifier: Modifier = Modifier,
    title: String,
    maxLength: Int,
    onChanged:(String)->Unit){
    OutlinedTextField (
        value = title,
        onValueChange = {onChanged(it)},
        label = { Text(stringResource(R.string.title_field_label))},
        placeholder = { Text(stringResource(R.string.title_field_placeholder)) },
        leadingIcon = { Icon(imageVector = Icons.Default.Task, contentDescription = null) },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Characters, /* STARTS WITH CAPS-MODE */
            keyboardType = KeyboardType.Text
        ),
        isError = title.isBlank(),
        colors = OutlinedTextFieldDefaults.colors(
            errorLabelColor = Color.Green.copy(alpha = 0.5f),
            errorBorderColor = Color.Green.copy(alpha = 0.5f),
        ),
        modifier = modifier,
        supportingText = {
            Box(modifier=Modifier.fillMaxWidth()){
                Text(
                    text = "${title.length}/${maxLength}",
                    modifier=Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    )
}

fun fromMillisToLocalDate(millis: Long): LocalDate{
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDatePicker(
    selectedDateMillis: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis ?: selectedDateMillis)
                onDismiss()
            }) { Text(stringResource(R.string.button_text_ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.button_text_cancel)) }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}