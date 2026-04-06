package com.s1g1.kantask.viewmodel.task

import com.s1g1.kantask.database.tasks.KanbanStatus
import com.s1g1.kantask.database.tasks.TaskEntity
import java.time.LocalDate

sealed interface TaskEvent {

    data class OnPageChanged(val page: Int) : TaskEvent
    data class AddTask(val taskEntity: TaskEntity) : TaskEvent
    data class UpdateTask(val taskEntity: TaskEntity) : TaskEvent
    data class DeleteTask(val taskEntity: TaskEntity) : TaskEvent
    data class ToggleTaskStatus(val taskEntity: TaskEntity) : TaskEvent
    data class ShowEditDialog(val taskEntity: TaskEntity) : TaskEvent
    data class HideEditDialog(val taskEntity: TaskEntity) : TaskEvent
    data class UpdateTaskKanbanStatus(val taskEntity: TaskEntity, val newKanbanStatus: KanbanStatus) : TaskEvent
    data class PostponeUndoneFromPast(val today: LocalDate) : TaskEvent
    object ToggleAddTaskDialog : TaskEvent
    object ToggleEditTaskDialog : TaskEvent
    object SetToday : TaskEvent

}