package com.livingtechusa.reflexion.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "Heirarchy", foreignKeys = arrayOf(
    ForeignKey(
        entity = Item::class,
        parentColumns = arrayOf("autogenPK"),
        childColumns = arrayOf("ITEM_PK"),
        onDelete = ForeignKey.CASCADE
    )
))
data class Heirarchy(
    @PrimaryKey(autoGenerate = true)
    val autogenPK: Long = 0,
    val ITEM_PK: Long, // FK - ITEM.autogenPK
    val isTopic: Boolean,
    val isSubTopic: Boolean,
    val parent: Long? // FK - ITEM.autogenPK
)