package com.s1g1.kantask.database.tasks

import androidx.compose.ui.graphics.Color
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

    val kanbanStatus: KanbanStatus = KanbanStatus.Todo,
    val priority: Priority = Priority.None,
    val shouldNotify: Boolean = false,

    val repeatEveryNDays: Int = 0
)

enum class Priority(
    val count: Int,
    val text: String,
    val color: Color
){
    High(3, "High", Color(0xFFC00000)),
    Medium(2, "Medium", Color(0xFFC0C000)),
    Low(1, "Low", Color(0xFF00C000)),
    None(0, "None", Color(0xFF8080FF));
    companion object {
        fun fromInt(count: Int) = entries.firstOrNull { it.count == count } ?: None
    }
}

sealed class KanbanStatus(
    val name: String,
    val color: Color
){
    object Todo : KanbanStatus(
        name="TODO",
        color=Color.Gray
    )
    object InProgress : KanbanStatus(
        name="IN PROGRESS",
        color=Color.Red.copy(green=0.5f)
    )
    object Done : KanbanStatus(
        name="DONE",
        color=Color.Green
    )
    fun getPrevColor():Color{
        val currentIndex = entries.indexOf(this)
        return if (currentIndex>0){
            entries[currentIndex-1].color
        }else{
            this.color
        }
    }
    fun getNextColor():Color{
        val currentIndex = entries.indexOf(this)
        return if (currentIndex<entries.size-1){
            entries[currentIndex+1].color
        }else{
            this.color
        }
    }
    companion object {

        val entries = listOf(Todo, InProgress, Done)
        fun fromString(status: String): KanbanStatus{
            return entries.find {it.name == status} ?: Todo
        }
    }
}