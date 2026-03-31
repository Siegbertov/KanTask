package com.s1g1.kantask.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [TaskEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(TaskConverters::class)           /* IMPORTANT */
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskEntityDao(): TaskEntityDao
}

val MIGRATION_1_2 = object : Migration(1, 2){
    override fun migrate(db: SupportSQLiteDatabase){
        db.execSQL("ALTER TABLE $TASK_TABLE_NAME ADD COLUMN kanbanStatus TEXT NOT NULL DEFAULT '${KanbanStatus.Todo.name}'")
    }
}