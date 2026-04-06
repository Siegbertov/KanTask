package com.s1g1.kantask.database.notes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteEntityDao {

    @Query("SELECT * FROM $NOTE_TABLE_NAME ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM $NOTE_TABLE_NAME WHERE id=:id")
    fun getNoteByID(id: Int) : NoteEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNote(noteEntity: NoteEntity)

    @Delete
    suspend fun deleteNote(noteEntity: NoteEntity)

    @Delete
    suspend fun deleteNotes(notes: List<NoteEntity>)

    @Query("DELETE FROM $NOTE_TABLE_NAME WHERE id = :id")
    suspend fun deleteNoteById(id: Int)

}