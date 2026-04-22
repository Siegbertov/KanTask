package com.s1g1.kantask.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
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
import androidx.glance.layout.Alignment

class DayTasksWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val app = context.applicationContext as KanTaskApp
        val repository = app.taskRepository
        val todayDay = LocalDate.now()
        val dayTasks = repository.getTasksByDay( day = todayDay).first()
        provideContent {
            Column(
                modifier = GlanceModifier
                    .background(Color.LightGray.copy(alpha = 0.8f))
                    .fillMaxSize()
                    .padding(12.dp)
            ){
                WidgetTitleRow( day = todayDay)

                TasksWidgetColumn(
                    tasks = dayTasks,
                    onTaskCheckboxToggle = { selectedTask ->

                    }
                )
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
    private fun TasksWidgetColumn(
        tasks: List<TaskEntity>,
        onTaskCheckboxToggle: (TaskEntity)->Unit
    ){

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
                        onCheckedChange = { onTaskCheckboxToggle(currentTask) }
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