package com.livingtechusa.reflexion.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.livingtechusa.reflexion.data.entities.LinkedList

@Dao
interface LinkedListDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setLinkedList(linkedList: LinkedList)

    @Query("Select * FROM LinkedList")
    suspend fun getAllLinkedLists(): LinkedList?

    @Query("Delete FROM LinkedList")
    suspend fun clearLinkedList()

    @Query("Select * FROM LinkedList WHERE title = :title AND ITEM_PK = :itemPK")
    suspend fun selectLinkedList(title: String, itemPK: Long): LinkedList?

    @Query("Delete FROM LinkedList WHERE title = :title AND ITEM_PK = :itemPK")
    suspend fun deleteLinkedList(title: String, itemPK: Long)

    @Query("UPDATE LinkedList SET 'index' = :index WHERE  title = :title AND ITEM_PK = :itemPK")
    suspend fun setLinkedListIndex(title: String, itemPK: Long, index: Int)

    @Query("Select * FROM LinkedList WHERE title =:parent")
    suspend fun selectChildLinkedLists(parent: Long): List<LinkedList?>

    @Query("UPDATE LinkedList SET title = :newTitle WHERE title = :title AND ITEM_PK = :itemPK")
    suspend fun renameLinkedList(title: String, itemPK: Long, newTitle: String)
}