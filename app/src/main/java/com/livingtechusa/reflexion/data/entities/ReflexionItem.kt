package com.livingtechusa.reflexion.data.entities

import android.util.EventLogTags
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
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
    var image: ByteArray? = null,
    var videoUri: String? = "",
    var videoUrl: String? = "",
    var parent: Long? = null,
    var hasChildren: Boolean? = true
): Parcelable {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReflexionItem

        if (autogenPK != other.autogenPK) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (detailedDescription != other.detailedDescription) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false
        if (videoUri != other.videoUri) return false
        if (videoUrl != other.videoUrl) return false
        if (parent != other.parent) return false
        if (hasChildren != other.hasChildren) return false

        return true
    }

    override fun hashCode(): Int {
        var result = autogenPK.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (detailedDescription?.hashCode() ?: 0)
        result = 31 * result + (image?.contentHashCode() ?: 0)
        result = 31 * result + (videoUri?.hashCode() ?: 0)
        result = 31 * result + (videoUrl?.hashCode() ?: 0)
        result = 31 * result + (parent?.hashCode() ?: 0)
        result = 31 * result + (hasChildren?.hashCode() ?: 0)
        return result
    }
}