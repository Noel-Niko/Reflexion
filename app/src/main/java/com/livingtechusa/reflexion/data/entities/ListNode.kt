package com.livingtechusa.reflexion.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "LinkedList",
    indices = [Index(value = ["nodePk"])])
data class ListNode(
    @PrimaryKey(autoGenerate = true)
    val nodePk: Long,
    val topic: Long,
    val title: String,
    val itemPK: Long, // FK - ITEM.autogenPK
    val parentPk: Long?,  // FK - ITEM.autogenPK
    val childPk: Long?     // FK - ITEM.autogenPK
)