package com.s1g1.kantask.viewmodel.task

import com.s1g1.kantask.database.tasks.TaskEntity
import java.time.LocalDate

data class TaskState(
    val selectedDate: LocalDate = LocalDate.now(),
    val tasks: List<TaskEntity> = emptyList(),
    val taskToEdit: TaskEntity? = null,
    val isAddTaskDialogVisible: Boolean = false,
    val isEditTaskDialogVisible: Boolean = false,
)