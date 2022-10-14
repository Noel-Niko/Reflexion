package com.livingtechusa.reflexion.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.livingtechusa.reflexion.data.entities.KeyWords

@Dao
interface KeyWordsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setKeyWords(user: KeyWords)

    @Query("Select * FROM KeyWords")
    suspend fun getKeyWords(): KeyWords?

    @Query("Delete FROM KeyWords")
    suspend fun clearKeyWords()

    @Query("Select * FROM KeyWords WHERE ITEM_PK = :item_pk")
    suspend fun selectKeyWords(item_pk: String): List<KeyWords?>

    @Query("Delete FROM KeyWords WHERE word = :word")
    suspend fun deleteKeyWord(word: String)

    @Query("UPDATE KeyWords SET word = :newWord WHERE word = :word")
    suspend fun renameKeyWord(word: String, newWord: String)
}