package com.livingtechusa.reflexion.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "LinkedList",
    primaryKeys = ["userEmail", "title"],
    foreignKeys = arrayOf(
        ForeignKey(
            entity = ReflexionItem::class,
            parentColumns = arrayOf("autogenPK"),
            childColumns = arrayOf("ITEM_PK"),
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class LinkedList(
    val userEmail: String,
    val title: String,
    val ITEM_PK: Long, // FK - ITEM.autogenPK
    val index: Int
)