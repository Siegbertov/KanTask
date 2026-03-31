package com.s1g1.kantask.viewmodel

import com.s1g1.kantask.database.KanbanStatus
import com.s1g1.kantask.database.TaskEntity

sealed interface TaskEvent {

    data class OnPageChanged(val page: Int) : TaskEvent

    data class AddTask(val taskEntity: TaskEntity) : TaskEvent
    data class UpdateTask(val taskEntity: TaskEntity) : TaskEvent
    data class DeleteTask(val taskEntity: TaskEntity) : TaskEvent
    data class ToggleTaskStatus(val taskEntity: TaskEntity) : TaskEvent

    data class ShowEditDialog(val taskEntity: TaskEntity) : TaskEvent

    data class HideEditDialog(val taskEntity: TaskEntity) : TaskEvent

    data class UpdateTaskKanbanStatus(val taskEntity: TaskEntity, val newKanbanStatus: KanbanStatus) : TaskEvent

    object ToggleAddTaskDialog : TaskEvent
    object ToggleEditTaskDialog : TaskEvent


}