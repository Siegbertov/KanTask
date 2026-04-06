package com.s1g1.kantask

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.room.Room
import com.s1g1.kantask.database.AppDatabase
import com.s1g1.kantask.database.tasks.TaskRepository
import com.s1g1.kantask.database.notes.NoteRepository
import com.s1g1.kantask.database.MIGRATION_1_2
import com.s1g1.kantask.database.MIGRATION_2_3
import com.s1g1.kantask.database.MIGRATION_3_4
import com.s1g1.kantask.database.MIGRATION_4_5
import com.s1g1.kantask.service.REMINDER_CHANNEL


class KanTaskApp : Application(){

    val db: AppDatabase by lazy{
        Room.databaseBuilder(
            context = this,
            klass = AppDatabase::class.java,
            name="tasks.db")
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5
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