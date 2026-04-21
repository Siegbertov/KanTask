package com.s1g1.kantask.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.ui.graphics.vector.ImageVector

enum class MenuDestination(
    val label: String,
    val icon: ImageVector,
) {
    NOTES("Notes", Icons.Default.Book),
    CALENDAR("Calendar", Icons.Default.CalendarMonth),
    KANBAN("Kanban", Icons.Default.ViewKanban),
}

const val ACTION_ADD_NEW_NOTE = "com.s1g1.kantask.ADD_NEW_NOTE"
const val ACTION_ADD_NEW_TASK = "com.s1g1.kantask.ADD_NEW_TASK"