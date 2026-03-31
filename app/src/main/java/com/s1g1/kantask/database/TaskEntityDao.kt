package com.s1g1.kantask.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TaskEntityDao {

    @Query("""SELECT * FROM $TASK_TABLE_NAME ORDER BY day ASC, isDone ASC, (time IS NULL) ASC, time ASC, priority DESC""")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("""SELECT * FROM $TASK_TABLE_NAME WHERE day=(:day) ORDER BY isDone ASC, (time IS NULL) ASC, time ASC, priority DESC""")
    fun getTasksByDay(day: LocalDate): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(taskEntity: TaskEntity) : Long

    @Delete
    suspend fun deleteTask(taskEntity: TaskEntity)

    @Query("""
        UPDATE $TASK_TABLE_NAME 
            SET day = :today  
                WHERE day < :today AND isDone = 0""")
    suspend fun postponeUndoneFromPast(today: LocalDate)
}