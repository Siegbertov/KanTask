package com.s1g1.kantask.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
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
import com.s1g1.kantask.ui.ACTION_OPEN_TASK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DayTasksWidget : GlanceAppWidget() {

    companion object {
        const val EXTRA_TASK_ID = "EXTRA_TASK_ID"
    }

    private suspend fun fetchFreshTasks(
        context: Context, selectedDay:
        LocalDate, direct:
        Boolean = false
    ) : List<TaskEntity>{
        val repository = (context.applicationContext as KanTaskApp).taskRepository
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
        val dayTasks = fetchFreshTasks(context=context, selectedDay=todayDay, direct=false)

        provideContent {
            GlanceTheme {
                MyContent(
                    context = context,
                    day = todayDay,
                    tasks = dayTasks,
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
        ){
            Column(
                modifier = GlanceModifier
                    .background(Color.DarkGray.copy(alpha = 0.8f))
                    .fillMaxSize()
                    .padding(12.dp)
            ){
                WidgetTitleRow( day = day )
                TasksWidgetColumn( context = context, tasks = tasks )
            }
            Image(
                provider = ImageProvider(R.drawable.ic_refresh),
                contentDescription = null,
                modifier = GlanceModifier
                    .cornerRadius(12.dp)
                    .padding(4.dp)
                    .clickable{
                        scope.launch { this@DayTasksWidget.updateAll(context) }
                        Log.d("GlanceUpdate", "Update called at ${System.currentTimeMillis()}")
                    }
            )
        }
    }

    @Composable
    private fun WidgetTitleRow(day: LocalDate){
        Row(
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically,
            modifier = GlanceModifier
                .fillMaxWidth()

        ){
            Text(
                modifier = GlanceModifier,
                text = day.format(DateTimeFormatter.ofPattern("dd MMM")).uppercase(),
            )
        }
    }

    @Composable
    private fun TasksWidgetColumn( context: Context,  tasks: List<TaskEntity> ){
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
                        modifier = GlanceModifier.background(Color.Green.copy(alpha = 0.7f))
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
            DayTasksWidget().update(context, glanceId)
            Log.d("GlanceUpdate", "[taskId: ${taskId}] - Update called at ${System.currentTimeMillis()}")
        }
    }
}