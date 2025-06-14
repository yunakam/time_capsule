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

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE notes ADD COLUMN score INTEGER NOT NULL DEFAULT 100")
        database.execSQL("ALTER TABLE notes ADD COLUMN lastUpdated INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE notes ADD COLUMN visitTimestamps TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 1. Create new table with new column names
        database.execSQL("""
            CREATE TABLE notes_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                text TEXT NOT NULL,
                saidWho TEXT,
                title TEXT,
                source TEXT,
                page TEXT,
                url TEXT,
                tags TEXT,
                createdAt INTEGER NOT NULL,
                lastVisitedAt INTEGER,
                visitCount INTEGER NOT NULL,
                score INTEGER NOT NULL,
                lastUpdated INTEGER NOT NULL,
                visitTimestamps TEXT NOT NULL
            )
        """.trimIndent())

        // 2. Copy data from old table to new table, mapping old columns to new ones
        database.execSQL("""
            INSERT INTO notes_new (id, text, saidWho, title, source, page, url, tags, createdAt, lastVisitedAt, visitCount, score, lastUpdated, visitTimestamps)
            SELECT id, text, author, sourceTitle, publisher, page, sourceUrl, tags, createdAt, lastVisitedAt, visitCount, score, lastUpdated, visitTimestamps FROM notes
        """.trimIndent())

        // 3. Drop old table
        database.execSQL("DROP TABLE notes")

        // 4. Rename new table to old table name
        database.execSQL("ALTER TABLE notes_new RENAME TO notes")
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add nullable category column to notes table
        database.execSQL("ALTER TABLE notes ADD COLUMN category TEXT")
        
        // Update categories based on existing data patterns
        // If source is not null, it's likely a BOOK
        database.execSQL("""
            UPDATE notes 
            SET category = 'BOOK' 
            WHERE source IS NOT NULL 
                AND source != ''
        """.trimIndent())

        // If url is not null, it's likely from the WEB
        database.execSQL("""
            UPDATE notes 
            SET category = 'WEB' 
            WHERE url IS NOT NULL 
                AND url != ''
        """.trimIndent())

        // If saidWho exists but no source/url, it's likely a TALK
        database.execSQL("""
            UPDATE notes 
            SET category = 'TALK' 
            WHERE saidWho IS NOT NULL 
                AND saidWho != '' 
                AND (source IS NULL OR source = '')
                AND (url IS NULL OR url = '')
        """.trimIndent())
        
        // Create source_bindings table
        database.execSQL("""
            CREATE TABLE source_bindings (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                saidWho TEXT NOT NULL,
                title TEXT NOT NULL,
                source TEXT
            )
        """.trimIndent())
        
        // Create indices for faster lookups
        database.execSQL("CREATE INDEX index_source_bindings_saidWho ON source_bindings(saidWho)")
        database.execSQL("CREATE INDEX index_source_bindings_title ON source_bindings(title)")
        database.execSQL("CREATE INDEX index_source_bindings_source ON source_bindings(source)")
        
        // Populate source_bindings table with existing data
        database.execSQL("""
            INSERT INTO source_bindings (saidWho, title, source)
            SELECT saidWho, title, source 
            FROM notes 
            WHERE saidWho IS NOT NULL 
                AND title IS NOT NULL
        """.trimIndent())
    }
}

@Database(
    entities = [Note::class, NoteVisit::class, SourceBinding::class],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun noteVisitDao(): NoteVisitDao
    abstract fun sourceBindingDao(): SourceBindingDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notes.db"
                )
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                    .build()
                    .also { INSTANCE = it }
            }

        // DestructiveMigration
//        fun getInstance(context: Context): AppDatabase =
//            INSTANCE ?: synchronized(this) {
//                INSTANCE ?: Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "notes.db"
//                )
//                .fallbackToDestructiveMigration() // wipes old database and creates a new one
//                .build()
//                .also { INSTANCE = it }
//            }

    }
}
