package xyz.DKMobile.QuickJot2

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(NoteEntity::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDAO
}
