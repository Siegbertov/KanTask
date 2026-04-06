package com.s1g1.kantask

import android.Manifest
import android.os.Build
import android.os.Bundle
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
import com.s1g1.kantask.ui.MainNavigationSuite
import com.s1g1.kantask.ui.theme.KanTaskTheme
import com.s1g1.kantask.viewmodel.NoteViewModel
import com.s1g1.kantask.viewmodel.NoteViewModelFactory
import com.s1g1.kantask.viewmodel.TaskViewModel
import com.s1g1.kantask.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    private val tvm by viewModels<TaskViewModel>{
        val app = application as KanTaskApp
        TaskViewModelFactory(app.taskRepository, app)
    }

    private val nvm by viewModels<NoteViewModel>{
        val app = application as KanTaskApp
        NoteViewModelFactory(app.noteRepository, app)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
//                MainNavGraph(
//                    tvm = tvm,
//                    isDarkTheme=isDarkTheme,
//                    onToggleThemeChange={isDarkTheme=!isDarkTheme}
//                    )
                MainNavigationSuite(
                    tvm = tvm,
                    nvm=nvm,
                    isDarkTheme=isDarkTheme,
                    onToggleThemeChange={isDarkTheme=!isDarkTheme}
                )
            }
        }
    }
}
