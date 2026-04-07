package com.s1g1.kantask.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.s1g1.kantask.R
import com.s1g1.kantask.ui.MenuDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanTaskTopBarNS(
    currentDestination: MenuDestination,
    isDarkTheme: Boolean,
    onToggleThemeChange: () -> Unit,
    onPostponeClick: () -> Unit,
    onTodayScroll: () -> Unit,
    isSelectionMode: Boolean,
    onDeleteSelected: () -> Unit,
    onUpdatePinStateSelected: () -> Unit,
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title={
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon ={
            IconButton(onClick={
                onToggleThemeChange()
            }){
                Icon(
                    imageVector = if(isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = null
                )
            }
        },
        actions={
            when(currentDestination){
                MenuDestination.CALENDAR -> {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        IconButton(onClick = {
                            onPostponeClick()
                        }){
                            Icon(imageVector = Icons.Default.Update, contentDescription = null)
                        }

                        IconButton(onClick = {
                            onTodayScroll()
                        }){
                            Icon(imageVector = Icons.Default.Today, contentDescription = null)
                        }
                    }
                }
                MenuDestination.NOTES -> {
                    if(isSelectionMode){
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            IconButton(onClick = {
                                onUpdatePinStateSelected()
                            }){
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = null,
                                    tint = Color.Yellow
                                )
                            }

                            IconButton(onClick = {
                                onDeleteSelected()
                            }){
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color.Red
                                    )
                            }
                        }
                    }
                }
                MenuDestination.KANBAN -> {

                }
            }
        }
    )
}