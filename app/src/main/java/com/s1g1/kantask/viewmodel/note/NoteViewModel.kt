package com.s1g1.kantask.viewmodel.note

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.s1g1.kantask.database.notes.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteViewModel(
    private val repository: NoteRepository,
    application: Application
) : AndroidViewModel(application){

    private val _state = MutableStateFlow(NoteState())
    val state = _state.asStateFlow()

    init{
        viewModelScope.launch {
            repository.getAllNotes().collect { allNotes ->
                _state.update { it.copy(notes = allNotes)}
            }
        }
    }

    fun onEvent(event: NoteEvent){
        when(event){
            is NoteEvent.AddNote -> {
                viewModelScope.launch(Dispatchers.IO){
                    repository.upsert(noteEntity = event.noteEntity)
                }
            }
            is NoteEvent.UpdateNote -> {
                viewModelScope.launch(Dispatchers.IO){
                    repository.upsert(noteEntity = event.noteEntity)
                }
            }
            is NoteEvent.DeleteNote -> {
                viewModelScope.launch(Dispatchers.IO){
                    repository.delete(noteEntity = event.noteEntity)
                }
            }
            is NoteEvent.DeleteNoteById -> {
                viewModelScope.launch(Dispatchers.IO){
                    repository.deleteNoteById(id = event.noteId)
                }
            }
            is NoteEvent.DeleteNotes -> {
                viewModelScope.launch(Dispatchers.IO){
                    repository.deleteNotes(notes = event.notes)
                }
            }
            is NoteEvent.ShowEditDialog -> {
                _state.update { it.copy(
                    noteToEdit = event.noteEntity,
                    isEditNoteDialogVisible = true
                    ) }
            }
            is NoteEvent.HideEditDialog -> {
                _state.update { it.copy(
                    noteToEdit = null,
                    isEditNoteDialogVisible = false
                    ) }
            }
            is NoteEvent.ToggleSelected -> {
                _state.update { it.copy(
                    selectedNotes = if (it.selectedNotes.contains(event.noteEntity)) {
                        it.selectedNotes - event.noteEntity
                    } else {
                        it.selectedNotes + event.noteEntity
                    }
                )}
            }

            NoteEvent.ToggleAddNoteDialog -> {
                _state.update { it.copy(isAddNoteDialogVisible = !_state.value.isAddNoteDialogVisible) }
            }
            NoteEvent.ToggleEditNoteDialog -> {
                _state.update { it.copy(isEditNoteDialogVisible = !_state.value.isEditNoteDialogVisible) }
            }

            NoteEvent.ToggleDeleteSelected -> {
                viewModelScope.launch(Dispatchers.IO){
                    repository.deleteNotes(notes = _state.value.selectedNotes)
                    withContext(Dispatchers.Main){
                        _state.update{
                            it.copy(selectedNotes = emptyList())
                        }
                    }
                }
            }
        }
    }
}


class NoteViewModelFactory(
    private val repository: NoteRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}