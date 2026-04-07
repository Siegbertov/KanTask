package com.s1g1.kantask.database.notes

import androidx.room.TypeConverter

class NoteConverters {

    /* NoteColor */
    @TypeConverter
    fun fromNoteColor(noteColor: NoteColor): String =  noteColor.name
    @TypeConverter
    fun toNoteColor(colorName: String): NoteColor = NoteColor.fromName(name=colorName)

}