package xyz.DKMobile.QuickJot

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val uid: Int,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "noteText") val noteText: String

)
