package com.s1g1.kantask

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.s1g1.kantask.ui.ACTION_ADD_NEW_NOTE
import com.s1g1.kantask.ui.ACTION_ADD_NEW_TASK
import com.s1g1.kantask.ui.ACTION_OPEN_TASK
import com.s1g1.kantask.ui.MainNavigationSuite
import com.s1g1.kantask.ui.theme.KanTaskTheme
import com.s1g1.kantask.viewmodel.note.NoteViewModel
import com.s1g1.kantask.viewmodel.note.NoteViewModelFactory
import com.s1g1.kantask.viewmodel.task.TaskViewModel
import com.s1g1.kantask.viewmodel.task.TaskViewModelFactory
import com.s1g1.kantask.ui.MenuDestination
import com.s1g1.kantask.viewmodel.note.NoteEvent
import com.s1g1.kantask.viewmodel.task.TaskEvent
import com.s1g1.kantask.widget.DayTasksWidget

class MainActivity : ComponentActivity() {
    private val tvm by viewModels<TaskViewModel>{
        val app = application as KanTaskApp
        TaskViewModelFactory(app.taskRepository, app)
    }

    private val nvm by viewModels<NoteViewModel>{
        val app = application as KanTaskApp
        NoteViewModelFactory(app.noteRepository, app)
    }

    private var currentDestination by mutableStateOf(MenuDestination.CALENDAR)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)

        setContent {

            /* TODO REFACTOR PERMISSION ACCESS  */
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    Toast.makeText(this, "WONT WORK", Toast.LENGTH_SHORT).show()
                }
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            var isDarkTheme by rememberSaveable { mutableStateOf(true) }

            KanTaskTheme(darkTheme = isDarkTheme) {
                MainNavigationSuite(
                    currentDestination = currentDestination,
                    tvm = tvm,
                    nvm = nvm,
                    isDarkTheme = isDarkTheme,
                    onToggleThemeChange = { isDarkTheme = !isDarkTheme },
                    setNewDDestination = { newDest -> currentDestination = newDest }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?){
        when(intent?.action){
            ACTION_ADD_NEW_NOTE -> {
                currentDestination = MenuDestination.NOTES
                nvm.onEvent(event = NoteEvent.ToggleAddNoteDialog)
            }

            ACTION_ADD_NEW_TASK -> {
                currentDestination = MenuDestination.CALENDAR
                tvm.onEvent( event = TaskEvent.ToggleAddTaskDialog )
            }

            ACTION_OPEN_TASK-> {
                currentDestination = MenuDestination.CALENDAR
                val taskId = intent.getLongExtra(DayTasksWidget.EXTRA_TASK_ID, -1L)
//                val currentTaskEntity = tvm.getTaskEntityById(taskId=taskId)
                Log.d("IntentHandling", "[currentId: $taskId]")
                tvm.onEvent(TaskEvent.ShowEditDialogById(taskId=taskId))
            }
        }
    }

}
