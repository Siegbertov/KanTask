package com.s1g1.kantask.database.tasks

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class TaskRepository(private val dao: TaskEntityDao) {

    fun getAllTasks(): Flow<List<TaskEntity>> = dao.getAllTasks()

    fun getTasksByDay(day: LocalDate): Flow<List<TaskEntity>> = dao.getTasksByDay(day=day)

    suspend fun upsertTask(taskEntity: TaskEntity):Long{
        return dao.upsertTask(taskEntity = taskEntity)
    }

    suspend fun deleteTask(taskEntity: TaskEntity) = dao.deleteTask(taskEntity = taskEntity)

    suspend fun postponeUndoneFromPast(today: LocalDate) = dao.postponeUndoneFromPast(today=today)

}