package com.livingtechusa.reflexion.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "LinkedList")
data class ListNode(
    @PrimaryKey(autoGenerate = true)
    val nodePk: Long,
    val title: String,
    val parentPk: Long?,  // FK - ITEM.autogenPK
    val childPk: Long?     // FK - ITEM.autogenPK
)