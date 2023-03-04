package com.livingtechusa.reflexion.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "LinkedList",
//    foreignKeys = [
//        ForeignKey(
//            entity = ReflexionItem::class,
//            parentColumns = arrayOf("autogenPk"),
//            childColumns = arrayOf("itemPK"),
//            onDelete = ForeignKey.CASCADE
//        )
//    ],
    indices = [
        Index(value = ["topic"]),
        Index(value = ["itemPK"]),
        Index(value = ["parentPk"]),
        Index(value = ["childPk"]),
    ]
)
data class ListNode(
    @PrimaryKey(autoGenerate = true)
    val nodePk: Long,
    val topic: Long,
    val title: String,
    val itemPK: Long, // FK - ITEM.autogenPK
    val parentPk: Long?,  // FK - ITEM.autogenPK
    val childPk: Long?     // FK - ITEM.autogenPK
)