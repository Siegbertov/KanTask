package com.s1g1.kantask.ui.graph

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.s1g1.kantask.database.notes.NoteColor

@Composable
fun SelectColorDialog(
    onColorSelected: (NoteColor) -> Unit,
    onDismiss: ()-> Unit,
) {
    Dialog(
        onDismissRequest = {onDismiss()}
    ){
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ){
            LazyRow(

            ) {
                items(NoteColor.entries){ currentColor ->
                    IconButton(
                        onClick = {
                            onColorSelected(currentColor)
                            onDismiss()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = currentColor.clr
                        )
                    }
                }
            }
        }
    }
}