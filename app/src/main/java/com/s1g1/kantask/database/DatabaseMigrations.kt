package com.s1g1.kantask.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.s1g1.kantask.database.notes.NOTE_TABLE_NAME
import com.s1g1.kantask.database.notes.NoteColor
import com.s1g1.kantask.database.tasks.KanbanStatus
import com.s1g1.kantask.database.tasks.Priority
import com.s1g1.kantask.database.tasks.TASK_TABLE_NAME

object DatabaseMigrations {

    val ALL_MIGRATIONS = arrayOf(
        object : Migration(1, 2){
            override fun migrate(db: SupportSQLiteDatabase){
                db.execSQL("ALTER TABLE $TASK_TABLE_NAME ADD COLUMN kanbanStatus TEXT NOT NULL DEFAULT '${KanbanStatus.Todo.name}'")
            }
        },

        object : Migration(2, 3){
            override fun migrate(db: SupportSQLiteDatabase){
                db.execSQL("ALTER TABLE $TASK_TABLE_NAME ADD COLUMN priority INTEGER NOT NULL DEFAULT '${Priority.None.count}'")
            }
        },

        object : Migration(3, 4){
            override fun migrate(db: SupportSQLiteDatabase){
                db.execSQL("ALTER TABLE $TASK_TABLE_NAME ADD COLUMN shouldNotify INTEGER NOT NULL DEFAULT 0")
            }
        },

        object : Migration(4, 5){
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
        },

        object : Migration(5, 6){
            override fun migrate(db: SupportSQLiteDatabase){
                db.execSQL("ALTER TABLE $NOTE_TABLE_NAME ADD COLUMN pinned INTEGER NOT NULL DEFAULT 0")
            }
        },

        object : Migration(6, 7){
            override fun migrate(db: SupportSQLiteDatabase){
                db.execSQL("ALTER TABLE $NOTE_TABLE_NAME ADD COLUMN color TEXT NOT NULL DEFAULT '${NoteColor.getDefault().name}'")
            }
        },

        object : Migration(7, 8){
            override fun migrate(db: SupportSQLiteDatabase){
                db.execSQL("ALTER TABLE $TASK_TABLE_NAME ADD COLUMN repeatEveryNDays INTEGER NOT NULL DEFAULT 0")
            }
        },

    )

    const val LATEST_VERSION = 8
}