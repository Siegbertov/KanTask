package com.s1g1.kantask.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.s1g1.kantask.R
import com.s1g1.kantask.database.KanbanStatus
import com.s1g1.kantask.database.TaskEntity
import com.s1g1.kantask.database.TaskRepository
import com.s1g1.kantask.service.TaskReminderWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class TaskViewModel(
    private val repository: TaskRepository,
    application: Application
) : AndroidViewModel(application) {

    val dayCount = 365
    var isProgrammaticScroll = false

    private val workManager = WorkManager.getInstance(application)

    private val _state = MutableStateFlow(TaskState())
    val state = _state.asStateFlow()

    init{
        viewModelScope.launch {
            repository.getAllTasks().collect { allTasks ->
                _state.update { it.copy(tasks = allTasks)}
            }
        }
    }

    private fun scheduleNotificationWithWorkManager(taskEntity: TaskEntity){
        workManager.cancelUniqueWork(taskEntity.id.toString())
        if (!taskEntity.shouldNotify || taskEntity.time == null) return

        val taskDateTime = LocalDateTime.of(taskEntity.day, taskEntity.time)
        val taskEpochMilli = taskDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val delayInSeconds = (taskEpochMilli - System.currentTimeMillis()) / 1000
        val appName = getApplication<Application>().getString(R.string.app_name)
        if (delayInSeconds > 0){
            val data = workDataOf(
                "TASK_ID" to taskEntity.id.toInt(),
                "TASK_TITLE" to "${appName}:",
                "TASK_TEXT" to taskEntity.title
            )
            val workRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                .setInitialDelay(delayInSeconds, TimeUnit.SECONDS)
                .setInputData(data)
                .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofSeconds(15))
                .build()
            workManager.enqueueUniqueWork(
                taskEntity.id.toString(),
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    fun onEvent(event: TaskEvent){
        when(event){
            is TaskEvent.AddTask -> {
                viewModelScope.launch {
                    val newId = repository.upsertTask(taskEntity = event.taskEntity)
                    scheduleNotificationWithWorkManager(event.taskEntity.copy(id = newId))
                }
            }
            is TaskEvent.UpdateTask -> {
                viewModelScope.launch {
                    repository.upsertTask(taskEntity = event.taskEntity)
                    scheduleNotificationWithWorkManager(event.taskEntity)
                }
            }
            is TaskEvent.DeleteTask -> {
                viewModelScope.launch {
                    repository.deleteTask(taskEntity = event.taskEntity)
                    workManager.cancelUniqueWork(event.taskEntity.id.toString())
                }
            }
            is TaskEvent.OnPageChanged -> {
                _state.update { it.copy(
                    selectedDate = LocalDate.now().plusDays((event.page - dayCount).toLong())
                    )
                }
            }
            is TaskEvent.ToggleTaskStatus -> {
                viewModelScope.launch {
                    if (event.taskEntity.isDone){
                        repository.upsertTask(event.taskEntity.copy(
                            isDone = false,
                            kanbanStatus = KanbanStatus.Todo
                        ))
                    } else {
                        repository.upsertTask(event.taskEntity.copy(
                            isDone = true,
                            kanbanStatus = KanbanStatus.Done
                        ))
                    }
                }
            }

            is TaskEvent.UpdateTaskKanbanStatus -> {
                viewModelScope.launch{
                    if(event.taskEntity.isDone){
                        when(event.newKanbanStatus){
                            KanbanStatus.Done -> {
                                repository.upsertTask(event.taskEntity.copy(
                                    kanbanStatus = event.newKanbanStatus,
                                ))
                            }
                            KanbanStatus.InProgress, KanbanStatus.Todo -> {
                                repository.upsertTask(event.taskEntity.copy(
                                    kanbanStatus = event.newKanbanStatus,
                                    isDone = false
                                ))
                            }
                        }
                    } else {
                        when(event.newKanbanStatus){
                            KanbanStatus.Done -> {
                                repository.upsertTask(event.taskEntity.copy(
                                    kanbanStatus = event.newKanbanStatus,
                                    isDone = true
                                ))
                            }
                            KanbanStatus.InProgress, KanbanStatus.Todo  -> {
                                repository.upsertTask(event.taskEntity.copy(
                                    kanbanStatus = event.newKanbanStatus,
                                ))
                            }
                        }
                    }
                }
            }

            is TaskEvent.PostponeUndoneFromPast -> {
                viewModelScope.launch {
                    repository.postponeUndoneFromPast(today = event.today)
                }
            }

            is TaskEvent.ShowEditDialog -> {
                _state.update{
                    it.copy(taskToEdit = event.taskEntity, isEditTaskDialogVisible = true)
                }
            }
            is TaskEvent.HideEditDialog -> {
                _state.update{it.copy(taskToEdit = null, isEditTaskDialogVisible = false)}
            }

            TaskEvent.ToggleAddTaskDialog -> {
                _state.value = _state.value.copy(isAddTaskDialogVisible = !_state.value.isAddTaskDialogVisible)
            }

            TaskEvent.ToggleEditTaskDialog -> {
                _state.value = _state.value.copy(isEditTaskDialogVisible = !_state.value.isEditTaskDialogVisible)
            }

        }
    }
}

class TaskViewModelFactory(
    private val repository: TaskRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}