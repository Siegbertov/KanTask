package com.s1g1.kantask.viewmodel

import com.s1g1.kantask.database.notes.NoteEntity


sealed interface NoteEvent {

    data class AddNote(val noteEntity: NoteEntity) : NoteEvent
    data class UpdateNote(val noteEntity: NoteEntity) : NoteEvent
    data class DeleteNote(val noteEntity: NoteEntity) : NoteEvent
    data class DeleteNoteById(val noteId: Int) : NoteEvent
    data class DeleteNotes(val notes: List<NoteEntity>) : NoteEvent
    data class ToggleSelected(val noteEntity: NoteEntity) : NoteEvent
    data class ShowEditDialog(val noteEntity: NoteEntity) : NoteEvent
    data class HideEditDialog(val noteEntity: NoteEntity) : NoteEvent
    object ToggleAddNoteDialog : NoteEvent
    object ToggleEditNoteDialog : NoteEvent

}