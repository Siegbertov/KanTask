package com.s1g1.kantask.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.s1g1.kantask.viewmodel.NoteEvent
import com.s1g1.kantask.viewmodel.NoteState
import com.s1g1.kantask.viewmodel.NoteViewModel

@Composable
fun NoteScreen(
    innerPadding: PaddingValues,
    nvm: NoteViewModel,
    uiNoteState: NoteState
) {
    val allNotes = uiNoteState.notes
    Column(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
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
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center

                    ){
                        Text(currentNote.title, modifier=Modifier.padding(vertical=2.dp))
                        Text(currentNote.description, modifier=Modifier.padding(vertical=2.dp))
                    }
                }
            }
        }
    }
    if(uiNoteState.isAddNoteDialogVisible){
        AddNewOrEditNoteDialog(
            noteEntity = null,
            onFinalAction = { noteEntity ->
                nvm.onEvent(NoteEvent.AddNote(noteEntity=noteEntity))
            },
            onDismiss = { nvm.onEvent(NoteEvent.ToggleAddNoteDialog) }
        )
    }
}