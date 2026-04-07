package com.s1g1.kantask.database.notes

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteEntityDao) {

    fun getAllNotes(): Flow<List<NoteEntity>> = dao.getAllNotes()

    suspend fun getNoteById(id: Int) : NoteEntity{
        return dao.getNoteByID(id=id)
    }

    suspend fun upsert(noteEntity: NoteEntity){
        dao.upsertNote(noteEntity=noteEntity)
    }

    suspend fun delete(noteEntity: NoteEntity){
        dao.deleteNote(noteEntity=noteEntity)
    }

    suspend fun deleteNotes(notes: List<NoteEntity>){
        dao.deleteNotes(notes=notes)
    }

    suspend fun deleteNoteById(id: Int){
        return dao.deleteNoteById(id=id)
    }

    suspend fun updateNotesPinState(ids: List<Int>){
        return dao.updateNotesPinState(ids=ids)
    }

    suspend fun updateNotesColor(ids: List<Int>, newColor: NoteColor){
        return dao.updateNotesColor(ids=ids, newColor=newColor)
    }
}