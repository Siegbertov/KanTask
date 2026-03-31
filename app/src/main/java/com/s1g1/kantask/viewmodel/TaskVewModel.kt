package com.s1g1.kantask.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.s1g1.kantask.database.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskViewModel(
    private val repository: TaskRepository,
    application: Application
) : AndroidViewModel(application) {

    val dayCount = 365
    var isProgrammaticScroll = false

    private val _state = MutableStateFlow(TaskState())
    val state = _state.asStateFlow()

    init{
        viewModelScope.launch {
            repository.getAllTasks().collect { allTasks ->
                _state.update { it.copy(tasks = allTasks)}
            }
        }
    }

    fun onEvent(event: TaskEvent){
        when(event){
            is TaskEvent.AddTask -> {
                viewModelScope.launch {
                    repository.upsertTask(taskEntity = event.taskEntity)
                }
            }
            is TaskEvent.UpdateTask -> {
                viewModelScope.launch {
                    repository.upsertTask(taskEntity = event.taskEntity)
                }
            }
            is TaskEvent.DeleteTask -> {
                viewModelScope.launch {
                    repository.deleteTask(taskEntity = event.taskEntity)
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
                    repository.upsertTask(
                        taskEntity = event.taskEntity.copy(
                            isDone = !event.taskEntity.isDone
                        )
                    )
                }
            }
            /* TODO SHOW + HIDE EDIT DIALOG */
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