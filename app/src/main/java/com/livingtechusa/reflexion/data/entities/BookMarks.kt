package com.livingtechusa.reflexion.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "BookMarks",
//
//    foreignKeys = arrayOf(
//        ForeignKey(
//            entity = ReflexionItem::class,
//            parentColumns = arrayOf("autogenPK"),
//            childColumns = arrayOf("ITEM_PK"),
//            onDelete = ForeignKey.CASCADE
//        ),
//        ForeignKey(
//            entity = ListNode::class,
//            parentColumns = arrayOf("nodePk"),
//            childColumns = arrayOf("LIST_PK"),
//            onDelete = ForeignKey.CASCADE
//        )
//    )
)
data class BookMarks(
    @PrimaryKey(autoGenerate = true)
    val autoGenPk: Long,
    val ITEM_PK: Long?, // FK - ITEM.autogenPK
    val LIST_PK: Long?, // FK - LIST.nodePk
    val title: String
)