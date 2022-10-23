package com.livingtechusa.reflexion.data.entities

import android.util.EventLogTags
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "ReflexionItem",
        foreignKeys = arrayOf(
            ForeignKey(
                entity = ReflexionItem::class,
                parentColumns = arrayOf("autogenPK"),
                childColumns = arrayOf("parent"),
                onDelete = ForeignKey.CASCADE
            )
        )

)
data class ReflexionItem(
    @PrimaryKey(autoGenerate = true)
    var autogenPK: Long = 0,
    var name: String = "",
    var description: String? = "",
    var detailedDescription: String? = "",
    val image: ByteArray? = null,
    val videoUri: String? = "",
    val videoUrl: String? = "",
    val parent: Long? = null,
    val hasChildren: Boolean? = true
) {
    companion object {
        val AUTOGEN_PK = "autogenPK"
        val NAME = "name"
        val DESCRIPTION = "description"
        val DETAILED_DESCRIPTION = "detailedDescription"
        val IMAGE = "image"
        val VIDEO_URI = "videoUri"
        val VIDEO_URL = "videoUrl"
        val PARENT = "parent"
        val SIBLINGS = "siblings"
        val HAS_CHILDREN = "hasChildren"
    }
}