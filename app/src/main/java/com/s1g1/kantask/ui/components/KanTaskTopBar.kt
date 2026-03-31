package com.s1g1.kantask.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.s1g1.kantask.R
import com.s1g1.kantask.ui.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanTaskTopBar(
    navController: NavHostController,
    currentRoute: String?,
    onPostponeClick:()->Unit,
){
    CenterAlignedTopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title={
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text= when(currentRoute){
                        Routes.DAYSCREEN -> {"(${stringResource(R.string.day_screen_title)})"}
                        Routes.KANBANSCREEN -> {"(${stringResource(R.string.kanban_screen_title)})"}
                        else -> {"(${stringResource(R.string.not_implemented_screen_title)})"}
                    },
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic
                )

            }
        },
        actions={
            when(currentRoute){
                Routes.DAYSCREEN -> {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                        ) {
                        IconButton(onClick = {
                            onPostponeClick()
                        }){
                            Icon(imageVector = Icons.Default.Update, contentDescription = null)
                        }
                        IconButton(onClick = {
                            navController.navigate(Routes.KANBANSCREEN)
                        }) {
                            Icon(imageVector = Icons.Default.ViewKanban, contentDescription = null)
                        }
                    }
                }
                Routes.KANBANSCREEN -> {
                    IconButton(onClick = {
                        navController.navigate(Routes.DAYSCREEN)
                    }) {
                        Icon(imageVector = Icons.Default.ViewDay, contentDescription = null)
                    }
                }
            }
        }
    )
}