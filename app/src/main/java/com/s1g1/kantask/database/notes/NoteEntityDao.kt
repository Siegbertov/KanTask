package com.s1g1.kantask.database.notes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteEntityDao {

    @Query("SELECT * FROM $NOTE_TABLE_NAME ORDER BY pinned DESC, timestamp DESC")
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

    @Query("UPDATE $NOTE_TABLE_NAME SET pinned=1-pinned WHERE id IN (:ids)")
    suspend fun updateNotesPinState(ids: List<Int>)

}