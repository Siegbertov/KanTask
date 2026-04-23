package com.s1g1.kantask.database.tasks

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate

class TaskRepository(private val dao: TaskEntityDao) {

    fun getAllTasks(): Flow<List<TaskEntity>> = dao.getAllTasks()

    fun getTasksByDay(day: LocalDate): Flow<List<TaskEntity>> = dao.getTasksByDay(day=day)
    fun getTasksByDayDirect(day: LocalDate): List<TaskEntity> = dao.getTasksByDayDirect(day=day)
    suspend fun getTaskById(taskId: Long): TaskEntity = dao.getTaskById(taskId=taskId)

    suspend fun upsertTask(taskEntity: TaskEntity):Long{
        return dao.upsertTask(taskEntity = taskEntity)
    }

    suspend fun deleteTask(taskEntity: TaskEntity) = dao.deleteTask(taskEntity = taskEntity)

    suspend fun postponeUndoneFromPast(today: LocalDate) = dao.postponeUndoneFromPast(today=today)

    suspend fun postponeTaskToNewDate(taskId: Long, newDate: LocalDate){
        dao.postponeTaskToNewDate(taskId = taskId, newDate = newDate)
    }

    suspend fun toggleTaskDoneByIdInsideWidget(taskId: Long): Boolean{
        return withContext(Dispatchers.IO){
            val result = dao.toggleTaskDoneByIdInsideWidget(taskId=taskId)
            result > 0
        }
    }

}