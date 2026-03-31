package com.s1g1.kantask.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.s1g1.kantask.ui.Routes


@Composable
fun FloatingActionButtons(
    currentRoute: String?,
    onAddTaskClick:()->Unit
) {
    if (currentRoute==Routes.DAYSCREEN){
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ){
            FloatingActionButton(
                onClick = { onAddTaskClick() },
                modifier = Modifier.size(50.dp)
            ) {Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task") }
        }
    }
}