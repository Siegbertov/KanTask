package com.s1g1.kantask.viewmodel.note

import com.s1g1.kantask.database.notes.NoteEntity

data class NoteState(
    val notes: List<NoteEntity> = emptyList(),
    val selectedNotes: List<NoteEntity> = emptyList(),
    val noteToEdit: NoteEntity? = null,
    val isAddNoteDialogVisible: Boolean = false,
    val isEditNoteDialogVisible: Boolean = false,
)