package com.livingtechusa.reflexion.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Item")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val autogenPK: Long = 0,
    val name: String,
    val Description: String?,
    val Detailed_Description: String?,
    val image: ByteArray?,
    val VIDEO_URI: String?,
    val VIDEO_URL: String?
)