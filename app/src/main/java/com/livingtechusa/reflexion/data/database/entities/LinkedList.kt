package com.livingtechusa.reflexion.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "LinkedList",
    primaryKeys = ["title"],
    foreignKeys = arrayOf(
        ForeignKey(
            entity = Item::class,
            parentColumns = arrayOf("autogenPK"),
            childColumns = arrayOf("ITEM_PK"),
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class LinkedList(
    val title: String,
    val ITEM_PK: Long, // FK - ITEM.autogenPK
    val index: Int
)