package com.livingtechusa.reflexion.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.livingtechusa.reflexion.util.ReflexionArrayItem

@Entity(tableName = "LinkedList")
data class ListNode(
    @PrimaryKey(autoGenerate = true)
    val nodePk: Long,
    val topic: Long,
    val title: String,
    val itemPK: Long, // FK - ITEM.autogenPK
    val parentPk: Long?,  // FK - ITEM.autogenPK
    val childPk: Long?     // FK - ITEM.autogenPK
)