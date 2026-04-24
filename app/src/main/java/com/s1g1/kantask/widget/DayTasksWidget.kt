package com.s1g1.kantask.widget

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.GlanceTheme.colors
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import com.s1g1.kantask.KanTaskApp
import com.s1g1.kantask.database.tasks.TaskEntity
import java.time.LocalDate
import com.s1g1.kantask.R
import java.time.format.DateTimeFormatter
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.updateAll
import androidx.glance.layout.Alignment
import kotlinx.coroutines.Dispatchers
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Box
import com.s1g1.kantask.MainActivity
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.layout.size
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle
import com.s1g1.kantask.ui.ACTION_OPEN_TASK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.glance.action.actionStartActivity
import androidx.glance.text.TextDecoration

const val WIDGET_TAG = "GlanceUpdate"
object DayTasksWidget : GlanceAppWidget() {

    const val EXTRA_TASK_ID = "EXTRA_TASK_ID"

    private suspend fun fetchFreshTasks(
        context: Context, selectedDay:
        LocalDate, direct:
        Boolean = false
    ) : List<TaskEntity>{
        val repository = (context.applicationContext as KanTaskApp).taskRepository
        Log.d(WIDGET_TAG, "Fetching data at ${System.currentTimeMillis()}")
        return withContext(Dispatchers.IO)
        {
            if (direct){
                repository.getTasksByDayDirect( day = selectedDay)
            } else {
                repository.getTasksByDay( day = selectedDay).first()
            }
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val todayDay = LocalDate.now()

        provideContent {
            GlanceTheme {
                val dayTasks = produceState(
                    initialValue = emptyList(),
                    key1 = System.currentTimeMillis()
                ){
                    value = fetchFreshTasks(context = context, selectedDay = todayDay, direct=true)
                }
                MyContent(
                    context = context,
                    day = todayDay,
                    tasks = dayTasks.value,
                )
            }
        }
    }

    @Composable
    private fun MyContent(context: Context, day: LocalDate, tasks: List<TaskEntity>) {
        val scope = rememberCoroutineScope()
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = GlanceModifier
                .then(
                    if (Build.VERSION.SDK_INT >= 31) {
                        GlanceModifier
                            .background(colors.widgetBackground)
                            .cornerRadius(16.dp)
                    }
                    else {
                        GlanceModifier.background(
                            ImageProvider(R.drawable.rounded_24dp),
                            colorFilter = ColorFilter.tint(colors.widgetBackground)
                        )
                    }
                )
                .padding(8.dp)
        ){
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(12.dp)
            ){
                TitleRow( day = day )
                TasksLazyColumn( context = context, tasks = tasks )
            }
            Image(
                provider = ImageProvider(R.drawable.ic_refresh),
                contentDescription = null,
                colorFilter = ColorFilter.tint(colors.onSurface),
                modifier = GlanceModifier
                    .cornerRadius(12.dp)
                    .padding(4.dp)
                    .clickable{
                        Log.d(WIDGET_TAG, "Clicked on refresh icon at ${System.currentTimeMillis()}")
                        scope.launch { this@DayTasksWidget.updateAll(context) }
                    }
            )
        }
    }

    @Composable
    private fun TitleRow(day: LocalDate){
        Row(
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically,
            modifier = GlanceModifier
                .fillMaxWidth()

        ){
            Box(
                contentAlignment = Alignment.Center,
                modifier = GlanceModifier
                    .size(30.dp)
                    .background(ImageProvider(R.drawable.widget_background))
                    .clickable( actionStartActivity<MainActivity>() )
                    .cornerRadius(16.dp)
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_launcher_foreground),
                    contentDescription = "App Icon",
                    colorFilter = ColorFilter.tint(colors.inverseOnSurface),
                    modifier = GlanceModifier
                        .size(40.dp)
                        .cornerRadius(12.dp)
                        .padding(4.dp)
                )
            }

            Row(
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                modifier = GlanceModifier
                    .fillMaxWidth()
            ){
                Text(
                    modifier = GlanceModifier,
                    text = day.format(DateTimeFormatter.ofPattern("dd MMM")).uppercase(),
                    style = TextStyle(
                        color = colors.onSurface,
                        fontSize = typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Bold
                    ),
                )
            }
        }
    }

    @Composable
    private fun TasksLazyColumn(context: Context, tasks: List<TaskEntity> ){
        LazyColumn(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            items(tasks){ currentTask ->
                Row(
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .clickable(
                            actionStartActivity(
                                Intent(context, MainActivity::class.java).apply {
                                    action = ACTION_OPEN_TASK
                                    putExtra(EXTRA_TASK_ID, currentTask.id)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                            )
                        )
                ){
                    CheckBox(
                        checked = currentTask.isDone,
                        onCheckedChange = actionRunCallback<ToggleTaskCheckboxAction>(
                            actionParametersOf(
                                ToggleTaskCheckboxAction.TaskIdKey to currentTask.id
                            )
                        )
                    )
                    Text(
                        text=currentTask.title,
                        modifier = GlanceModifier,
                        style = TextStyle(
                            color = colors.onSurface,
                            fontStyle = FontStyle.Italic,
                            textDecoration = if (currentTask.isDone) TextDecoration.LineThrough else TextDecoration.None
                        ),
                    )
                }
            }
        }
    }

}

class ToggleTaskCheckboxAction : ActionCallback{
    companion object {
        val TaskIdKey = ActionParameters.Key<Long>("task_id_key")
    }
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val taskId = parameters[TaskIdKey] ?: return
        val repository = (context.applicationContext as KanTaskApp).taskRepository
        val isSuccess = repository.toggleTaskDoneByIdInsideWidget(taskId = taskId)
        if(isSuccess){
            Log.d(WIDGET_TAG, "Clicked on checkbox for [taskId: ${taskId}] at ${System.currentTimeMillis()}")
//            DayTasksWidget.update(context, glanceId)
            DayTasksWidget.updateAll(context)
        }
    }
}