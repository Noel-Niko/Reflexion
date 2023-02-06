package com.livingtechusa.reflexion.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.livingtechusa.reflexion.data.entities.Bookmarks

@Dao
interface BookMarksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setBookMarks(bookMark: Bookmarks)

    @Query("Select * FROM BookMarks")
    suspend fun getBookMarks(): List<Bookmarks?>

    @Query("Delete FROM BookMarks")
    suspend fun clearBookMarks()

    @Query("Select * FROM BookMarks WHERE ITEM_PK = :item_pk")
    suspend fun selectItemBookMark(item_pk: Long): Bookmarks?

    @Query("Select * FROM BookMarks WHERE LIST_PK = :list_pk")
    suspend fun selectListBookMarks(list_pk: Long): List<Bookmarks?>

    @Query("Select * FROM BookMarks WHERE LEVEL_PK IS NOT NULL")
    suspend fun selectLevelBookMarks(): List<Bookmarks?>

    @Query("Delete FROM BookMarks WHERE autoGenPk = :autoGenPk")
    suspend fun deleteBookmark(autoGenPk: Long)

    @Query("Delete FROM BookMarks WHERE title = :word")
    suspend fun deleteKeyWord(word: String)

    @Query("UPDATE BookMarks SET title = :newWord WHERE title = :word")
    suspend fun renameKeyWord(word: String, newWord: String)


    @Query("Select * FROM BookMarks WHERE title LIKE :text || '%' order by title ASC")
    suspend fun searchBookmarksByTitle(text: String): List<Bookmarks?>
    @Query("Select * FROM BookMarks WHERE LEVEL_PK = :levelPk")
    suspend fun selectBookmarkByLevelPK(levelPk: Long?): Bookmarks?

}