package com.s1g1.kantask.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.s1g1.kantask.database.notes.NOTE_TABLE_NAME
import com.s1g1.kantask.database.notes.NoteEntity
import com.s1g1.kantask.database.tasks.KanbanStatus
import com.s1g1.kantask.database.tasks.Priority
import com.s1g1.kantask.database.tasks.TASK_TABLE_NAME
import com.s1g1.kantask.database.tasks.TaskConverters
import com.s1g1.kantask.database.tasks.TaskEntity
import com.s1g1.kantask.database.tasks.TaskEntityDao

@Database(
    entities = [
        TaskEntity::class,
        NoteEntity::class,
               ],
    version = 5,
    exportSchema = true
)
@TypeConverters(TaskConverters::class)           /* IMPORTANT */
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskEntityDao(): TaskEntityDao
}

val MIGRATION_1_2 = object : Migration(1, 2){
    override fun migrate(db: SupportSQLiteDatabase){
        db.execSQL("ALTER TABLE ${TASK_TABLE_NAME} ADD COLUMN kanbanStatus TEXT NOT NULL DEFAULT '${KanbanStatus.Todo.name}'")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3){
    override fun migrate(db: SupportSQLiteDatabase){
        db.execSQL("ALTER TABLE ${TASK_TABLE_NAME} ADD COLUMN priority INTEGER NOT NULL DEFAULT '${Priority.None.count}'")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4){
    override fun migrate(db: SupportSQLiteDatabase){
        db.execSQL("ALTER TABLE ${TASK_TABLE_NAME} ADD COLUMN shouldNotify INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5){
    override fun migrate(db: SupportSQLiteDatabase){
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `${NOTE_TABLE_NAME}` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `title` TEXT NOT NULL DEFAULT '', 
                `description` TEXT NOT NULL DEFAULT '', 
                `timestamp` INTEGER NOT NULL
            )
        """.trimIndent())
    }
}
