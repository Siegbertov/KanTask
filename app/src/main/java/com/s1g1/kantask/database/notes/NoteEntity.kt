package com.s1g1.kantask.database.notes

import androidx.room.Entity
import androidx.room.PrimaryKey

const val NOTE_TABLE_NAME = "notes_table"

@Entity(tableName = NOTE_TABLE_NAME)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val timestamp: Long,
)
