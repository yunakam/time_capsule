package com.example.timecapsule.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // No-op: schema is compatible
    }
}

@Database(
    entities = [Note::class, NoteVisit::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun noteVisitDao(): NoteVisitDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

//        fun getInstance(context: Context): AppDatabase =
//            INSTANCE ?: synchronized(this) {
//                INSTANCE ?: Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "notes.db"
//                )
//                    .addMigrations(MIGRATION_3_4)
//                    .build()
//                    .also { INSTANCE = it }
//            }
        // DestructiveMigration
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notes.db"
                )
                .fallbackToDestructiveMigration() // wipes old database and creates a new one
                .build()
                .also { INSTANCE = it }
            }
    }
}