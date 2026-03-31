package com.s1g1.kantask.ui.graph

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.s1g1.kantask.R
import com.s1g1.kantask.viewmodel.TaskState
import com.s1g1.kantask.viewmodel.TaskViewModel

@Composable
fun KanbanScreen(
    innerPadding: PaddingValues,
    tvm: TaskViewModel,
    uiState: TaskState
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier=Modifier.fillMaxSize().padding(innerPadding)
    ){
        Text(stringResource(R.string.kanban_screen_title).uppercase())
    }
}