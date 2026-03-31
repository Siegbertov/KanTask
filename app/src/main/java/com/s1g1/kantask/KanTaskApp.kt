package com.s1g1.kantask

import android.app.Application
import androidx.room.Room
import com.s1g1.kantask.database.AppDatabase
import com.s1g1.kantask.database.MIGRATION_1_2
import com.s1g1.kantask.database.MIGRATION_2_3
import com.s1g1.kantask.database.TaskRepository


class KanTaskApp : Application(){

    val db: AppDatabase by lazy{
        Room.databaseBuilder(
            context = this,
            klass = AppDatabase::class.java,
            name="tasks.db")
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3
            )
            .build()
    }

    val repository by lazy { TaskRepository(dao = db.taskEntityDao()) }
}