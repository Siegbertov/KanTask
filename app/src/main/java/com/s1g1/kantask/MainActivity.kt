package com.s1g1.kantask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.s1g1.kantask.ui.MainNavGraph
import com.s1g1.kantask.ui.theme.KanTaskTheme
import com.s1g1.kantask.viewmodel.TaskViewModel
import com.s1g1.kantask.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    private val tvm by viewModels<TaskViewModel>{
        val app = application as KanTaskApp
        TaskViewModelFactory(app.repository, app)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KanTaskTheme {
                MainNavGraph( tvm = tvm )
            }
        }
    }
}
