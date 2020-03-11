package xyz.DKMobile.QuickJot2

import androidx.room.*

@Dao
interface NoteDAO {
    @Query("SELECT * FROM Notes")
    suspend fun getAll(): List<NoteEntity>

    @Query("SELECT * FROM Notes WHERE uid LIKE :uid")
    suspend fun getByuID(uid: Int): NoteEntity

    @Query("SELECT * FROM Notes WHERE category LIKE :category")
    suspend fun getByCategory(category: String): List<NoteEntity>

    @Insert
    suspend fun insert(note: NoteEntity):Long

//SELECT ROWID from SQL_LITE_SEQUENCE order by ROWID DESC limit 1

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)
}