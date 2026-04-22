package com.s1g1.kantask.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
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
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.updateAll
import androidx.glance.layout.Alignment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class DayTasksWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val app = context.applicationContext as KanTaskApp
        val repository = app.taskRepository
        val todayDay = LocalDate.now()
        val dayTasks = withContext(Dispatchers.IO){
            repository.getTasksByDay( day = todayDay).first()
        }
        provideContent {
            Column(
                modifier = GlanceModifier
                    .background(Color.LightGray.copy(alpha = 0.8f))
                    .fillMaxSize()
                    .padding(12.dp)
            ){
                WidgetTitleRow( day = todayDay)

                TasksWidgetColumn( tasks = dayTasks )
            }
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
    private fun TasksWidgetColumn( tasks: List<TaskEntity> ){

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

        val app = context.applicationContext as KanTaskApp
        val repository = app.taskRepository

        withContext(Dispatchers.IO){
            repository.toggleTaskDoneById(taskId = taskId)
            delay(100)
        }
        DayTasksWidget().updateAll(context)
    }
}