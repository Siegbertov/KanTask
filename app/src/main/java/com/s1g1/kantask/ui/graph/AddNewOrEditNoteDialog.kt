package com.s1g1.kantask.ui.graph

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.s1g1.kantask.R
import com.s1g1.kantask.database.notes.NoteColor
import com.s1g1.kantask.database.notes.NoteEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class NoteFormState(
    val title: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
){
    fun fromNote(noteEntity: NoteEntity): NoteFormState{
        return this.copy(
            title = noteEntity.title,
            description = noteEntity.description
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewOrEditNoteDialog(
    noteEntity: NoteEntity?,
    onFinalAction:(NoteEntity)->Unit,
    onDismiss:()->Unit
) {
    val scrollState = rememberScrollState()
    var formState by remember {
        val initial = NoteFormState()
        mutableStateOf(if (noteEntity != null) initial.fromNote(noteEntity) else initial)
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {Text(text = if(noteEntity==null){
                stringResource(R.string.title_add_note)
            }else{stringResource(R.string.title_edit_note)}) }
        },
        text = {
            Column(
                modifier=Modifier.fillMaxWidth().verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ){
                TimestampComponent(
                    timestamp = formState.timestamp
                )
                TitleComponent(
                    title = formState.title,
                    onChanged = { newTitle ->
                        formState = formState.copy(title = newTitle)
                    }
                )
                DescriptionComponent(
                    description = formState.description,
                    onChanged = { newDescription ->
                        formState = formState.copy(description = newDescription)
                    }
                )
            }
        },
        confirmButton = {
            OutlinedButton(onClick = {
                if (formState.title.isNotBlank() && (noteEntity?.title != formState.title || noteEntity.description != formState.description)){
                    onFinalAction(
                        NoteEntity(
                            id = if(noteEntity!=null){noteEntity.id}else{0},
                            title = formState.title,
                            description = formState.description,
                            timestamp = formState.timestamp,
                            pinned = if(noteEntity!=null) {noteEntity.pinned} else {false},
                            color = if(noteEntity!=null) {noteEntity.color} else {NoteColor.getDefault()}
                        )
                    )
                    onDismiss()
                }
            }) {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
                    Text(text= if(noteEntity==null){ stringResource(R.string.button_text_add) }else{stringResource(R.string.button_text_edit)})
                }
            }
        }
    )
}

@Composable
fun DescriptionComponent(
    description: String,
    onChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = description,
        onValueChange = {onChanged(it)},
        label = { Text(stringResource(R.string.description_field_label))},
        placeholder = { Text(stringResource(R.string.description_field_placeholder), fontStyle = FontStyle.Italic)},
        modifier = Modifier.heightIn(min = 150.dp)
    )
}

@Composable
fun TimestampComponent(
    timestamp: Long
) {
    val sdf = SimpleDateFormat("d MMM, HH:mm:ss", Locale.getDefault())
    val formattedDate = sdf.format(Date(timestamp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ){
        Text(text=formattedDate)
    }
}

@Composable
fun TitleComponent(
    title: String,
    onChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = title,
        onValueChange = {onChanged(it)},
        label = { Text(stringResource(R.string.title_field_label))},
        placeholder = { Text(stringResource(R.string.title_field_placeholder)) },
    )
}