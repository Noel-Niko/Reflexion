package com.livingtechusa.reflexion.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ItemImageAssociativeData",
    indices = [Index(value = ["imagePk"])],
)
data class ItemImageAssociativeData(
    @PrimaryKey
    val itemPk: Long,
    val imagePk: Long,
)
