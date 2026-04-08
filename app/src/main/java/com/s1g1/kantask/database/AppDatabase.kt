package com.s1g1.kantask.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.s1g1.kantask.database.notes.NoteConverters
import com.s1g1.kantask.database.notes.NoteEntity
import com.s1g1.kantask.database.notes.NoteEntityDao
import com.s1g1.kantask.database.tasks.TaskConverters
import com.s1g1.kantask.database.tasks.TaskEntity
import com.s1g1.kantask.database.tasks.TaskEntityDao

@Database(
    entities = [
        TaskEntity::class,
        NoteEntity::class,
               ],
    version = DatabaseMigrations.LATEST_VERSION,
    exportSchema = true
)
@TypeConverters(
    value = [
        TaskConverters::class,
        NoteConverters::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskEntityDao(): TaskEntityDao
    abstract fun noteEntityDao(): NoteEntityDao
}


