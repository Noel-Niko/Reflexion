package com.livingtechusa.reflexion.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "KeyWords",
    primaryKeys = ["ITEM_PK", "word"],
    foreignKeys = arrayOf(
        ForeignKey(
            entity = Item::class,
            parentColumns = arrayOf("autogenPK"),
            childColumns = arrayOf("ITEM_PK"),
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class KeyWords(
    val ITEM_PK: Long, // FK - ITEM.autogenPK
    val word: String
)