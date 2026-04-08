package com.s1g1.kantask

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.room.Room
import com.s1g1.kantask.database.AppDatabase
import com.s1g1.kantask.database.DatabaseMigrations
import com.s1g1.kantask.database.tasks.TaskRepository
import com.s1g1.kantask.database.notes.NoteRepository
import com.s1g1.kantask.service.REMINDER_CHANNEL


class KanTaskApp : Application(){

    val db: AppDatabase by lazy{
        Room.databaseBuilder(
            context = this,
            klass = AppDatabase::class.java,
            name="tasks.db")
            .addMigrations(
                *DatabaseMigrations.ALL_MIGRATIONS
            )
            .build()
    }

    val taskRepository by lazy { TaskRepository(dao = db.taskEntityDao()) }

    val noteRepository by lazy { NoteRepository(dao = db.noteEntityDao()) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelId = REMINDER_CHANNEL
            val name = "Task Reminders"
            val descriptionText = "Notifications for scheduled tasks"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}