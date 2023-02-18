package com.livingtechusa.reflexion.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "BookMarks"
)
data class Bookmarks(
    @PrimaryKey(autoGenerate = true)
    val autoGenPk: Long,
    val ITEM_PK: Long?, // FK - ITEM.autogenPK
    val LIST_PK: Long?, // FK - LIST.nodePk
    val LEVEL_PK: Long?, // FK of parent of a level to display
    val title: String
)