package com.livingtechusa.reflexion.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.livingtechusa.reflexion.data.entities.ListNode

@Dao
interface LinkedListDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewNode(listNode: ListNode): Long

    @Query("Select topic FROM LinkedList WHERE itemPK = :itemPk")
    suspend fun selectNodeTopic(itemPk: Long): Long?

    @Query("Select * FROM LinkedList WHERE topic =:topicPk AND parentPk IS NULL")
    suspend fun selectNodeHeadsByTopic(topicPk: Long): List<ListNode?>

    @Query("Select * FROM LinkedList WHERE topic =:topicPk ")
    suspend fun selectAllNodesByTopic(topicPk: Long): List<ListNode?>

    @Query("Select * FROM LinkedList WHERE parentPk =:nodePk")
    suspend fun selectChildNode(nodePk: Long): ListNode?

    @Query("Select * FROM LinkedList WHERE parentPk =:parentPk")
    suspend fun selectParentNode(parentPk: Long): ListNode?

    @Query("UPDATE LinkedList SET title = :title, parentPk = :parentPK, childPk = :childPk WHERE nodePk = :nodePk")
    suspend fun updateListNode(nodePk: Long, title: String, parentPK: Long, childPk: Long)

    @Query("Select * FROM LinkedList WHERE parentPk IS NULL")
    suspend fun getAllLinkedLists(): List<ListNode?>

    @Query("Delete FROM LinkedList")
    suspend fun deleteAllLinkedLists()

    @Query("Select * FROM LinkedList WHERE nodePk = :nodePk")
    suspend fun selectListNode(nodePk: Long): ListNode?

    @Query("Delete FROM LinkedList WHERE nodePk = :nodePk")
    suspend fun deleteSelectedNode(nodePk: Long)
}