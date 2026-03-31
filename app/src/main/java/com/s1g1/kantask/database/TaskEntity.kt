package com.s1g1.kantask.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

const val TASK_TABLE_NAME = "todotasks"

@Entity(tableName = TASK_TABLE_NAME)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val day: LocalDate,
    val isDone: Boolean = false,
    val title: String,
    val description: String?,
    val time: LocalTime?,
    val duration: Duration?,

//    val kanbanStatus: KanbanStatus, // TODO: implement
//    val priority: Priority, // TODO: implement
//    val shouldNotify: Boolean = false, // TODO: implement
)