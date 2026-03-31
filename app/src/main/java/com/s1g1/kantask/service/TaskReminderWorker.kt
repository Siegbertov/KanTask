package com.s1g1.kantask.service

import android.R
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters


const val REMINDER_CHANNEL =  "Reminders"

class TaskReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    appContext = context,
    params = workerParams
) {
    override suspend fun doWork(): Result {
        val taskId = inputData.getInt("TASK_ID", 0)
        val taskTitle = inputData.getString("TASK_TITLE") ?: "REMINDER"
        val taskText = inputData.getString("TASK_TEXT") ?: "TEXT"
        showNotification(taskId, taskTitle, taskText)
        return Result.success()
    }

    private fun showNotification(id: Int, title: String, taskText: String){
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(
            applicationContext,
            REMINDER_CHANNEL
        )
            .setSmallIcon(R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(taskText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id, notification)
    }
}