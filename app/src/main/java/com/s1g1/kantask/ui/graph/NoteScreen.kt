package com.s1g1.kantask.ui.graph

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.s1g1.kantask.viewmodel.note.NoteEvent
import com.s1g1.kantask.viewmodel.note.NoteState
import com.s1g1.kantask.viewmodel.note.NoteViewModel

@Composable
fun NoteScreen(
    innerPadding: PaddingValues,
    nvm: NoteViewModel,
    uiNoteState: NoteState
) {
    val allNotes = uiNoteState.notes
    val isSelectionMode = uiNoteState.selectedNotes.isNotEmpty()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        if(allNotes.isEmpty()){
            Text("NO NOTES")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(allNotes){ currentNote ->
                    NoteRow(
                        currentNote = currentNote,
                        isSelectionMode = uiNoteState.selectedNotes.isNotEmpty(),
                        isSelected = uiNoteState.selectedNotes.contains(currentNote),
                        onRBClick = { currNote ->
                            nvm.onEvent(NoteEvent.ToggleSelected(noteEntity = currNote))
                        },
                        onClickNote = { currNote ->
                            if (isSelectionMode) {
                                nvm.onEvent(NoteEvent.ToggleSelected(noteEntity = currNote))
                            } else {
                                nvm.onEvent(NoteEvent.ShowEditDialog(noteEntity = currNote))
                            }
                        },
                        onLongClickNote = { currNote ->
                            if (isSelectionMode) {

                            } else {
                                nvm.onEvent(NoteEvent.ToggleSelected(noteEntity = currNote))
                            }
                        }
                    )
                }
            }
        }
    }
    if(uiNoteState.isAddNoteDialogVisible){
        AddNewOrEditNoteDialog(
            noteEntity = null,
            onFinalAction = { noteEntity ->
                nvm.onEvent(NoteEvent.AddNote(noteEntity = noteEntity))
            },
            onDismiss = { nvm.onEvent(NoteEvent.ToggleAddNoteDialog) }
        )
    }
    if(uiNoteState.isEditNoteDialogVisible){
        AddNewOrEditNoteDialog(
            noteEntity = uiNoteState.noteToEdit,
            onFinalAction = { noteEntity ->
                nvm.onEvent(NoteEvent.UpdateNote(noteEntity = noteEntity))
            },
            onDismiss = { nvm.onEvent(NoteEvent.ToggleEditNoteDialog) }
        )
    }
}