package com.livingtechusa.reflexion.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "BookMarks",
    foreignKeys = [ForeignKey(
        entity = ReflexionItem::class,
        parentColumns = arrayOf("autogenPk"),
        childColumns = arrayOf("ITEM_PK"),
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = ListNode::class,
        parentColumns = arrayOf("nodePk"),
        childColumns = arrayOf("LIST_PK"),
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = ReflexionItem::class,
        parentColumns = arrayOf("autogenPk"),
        childColumns = arrayOf("LEVEL_PK"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["ITEM_PK"]),
        Index(value = ["LIST_PK"]),
        Index(value = ["LEVEL_PK"]),
    ]
)
data class Bookmarks(
    @PrimaryKey(autoGenerate = true)
    val autoGenPk: Long,
    val ITEM_PK: Long?, // FK - ITEM.autogenPK
    val LIST_PK: Long?, // FK - LIST.nodePk
    val LEVEL_PK: Long?, // FK of parent of a level to display
    val title: String
)