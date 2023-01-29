package com.livingtechusa.reflexion.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.livingtechusa.reflexion.data.entities.BookMarks

@Dao
interface BookMarksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setBookMarks(bookMark: BookMarks)

    @Query("Select * FROM BookMarks")
    suspend fun getBookMarks(): List<BookMarks?>

    @Query("Delete FROM BookMarks")
    suspend fun clearBookMarks()

    @Query("Select * FROM BookMarks WHERE ITEM_PK = :item_pk")
    suspend fun selectItemBookMarks(item_pk: Long): List<BookMarks?>

    @Query("Select * FROM BookMarks WHERE LIST_PK = :list_pk")
    suspend fun selectListBookMarks(list_pk: Long): List<BookMarks?>

    @Query("Delete FROM BookMarks WHERE autoGenPk = :autoGenPk")
    suspend fun deleteBookmark(autoGenPk: Long)

    @Query("Delete FROM BookMarks WHERE title = :word")
    suspend fun deleteKeyWord(word: String)

    @Query("UPDATE BookMarks SET title = :newWord WHERE title = :word")
    suspend fun renameKeyWord(word: String, newWord: String)


    @Query("Select * FROM BookMarks WHERE title LIKE :text || '%' order by title ASC")
    suspend fun searchBookmarksByTitle(text: String): List<BookMarks?>
}