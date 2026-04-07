package com.s1g1.kantask.database.notes

import androidx.compose.ui.graphics.Color
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
    val pinned: Boolean = false,

    val color: NoteColor = NoteColor.getDefault()
)

enum class NoteColor(val clr: Color){

    WHITE(clr = Color(0xFFFFFFFF)),
    RED(clr = Color(0xFFF28B82)),
    ORANGE(clr = Color(0xFFFBBC04)),
    YELLOW(clr = Color(0xFFFFF475)),
    GREEN(clr = Color(0xFFCCFF90)),
    BLUE(clr = Color(0xFFAECBFA)),
    PURPLE(clr = Color(0xFFD7AEFB));

    companion object{
        fun fromName(name: String): NoteColor{
            return entries.find{it.name == name}?: getDefault()
        }

        fun getDefault():NoteColor = NoteColor.WHITE
    }
}

