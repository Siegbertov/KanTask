package com.s1g1.kantask.ui.graph

import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.s1g1.kantask.database.notes.NoteEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NoteRow(
    currentNote: NoteEntity,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onRBClick: (NoteEntity) -> Unit,
    onClickNote: (NoteEntity) -> Unit,
    onLongClickNote: (NoteEntity) -> Unit,
){
    val cardShape = RoundedCornerShape(size = 16.dp)
    val sdf = SimpleDateFormat("d MMM, HH:mm", Locale.getDefault())
    val formattedDate = sdf.format(Date(currentNote.timestamp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .heightIn(min = 100.dp)
            .padding(4.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = cardShape
            )
            .clip(cardShape)
            .combinedClickable(
                onClick = { onClickNote(currentNote) },
                onLongClick = { onLongClickNote(currentNote) }
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ){
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center

            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ){
                    Text(currentNote.title, modifier=Modifier.padding(vertical=2.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ){
                    Text(currentNote.description, modifier=Modifier.padding(vertical=2.dp))
                }
            }
            Surface(
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.TopEnd),
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF808080)
            ) {
                Text(
                    text = "Last updated:\n ${formattedDate}",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    textAlign = TextAlign.End
                )
            }
            if(isSelectionMode){
                Surface(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.CenterEnd),
                    color = Color.Transparent
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = {
                            onRBClick(currentNote)
                        }
                    )
                }
            }
        }
    }
}