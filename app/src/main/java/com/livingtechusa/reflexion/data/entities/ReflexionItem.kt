package com.livingtechusa.reflexion.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.livingtechusa.reflexion.util.Constants.EMPTY_ITEM
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "ReflexionItem",
    foreignKeys = [ForeignKey(
        entity = Image::class,
        parentColumns = arrayOf("imagePk"),
        childColumns = arrayOf("imagePk"),
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [
        Index(value = ["autogenPk"]),
        Index(value = ["parent"]),
        Index(value = ["videoUri"]),
        Index(value = ["imagePk"]),
    ]
)
data class ReflexionItem(
    @PrimaryKey(autoGenerate = true)
    var autogenPk: Long = 0,
    var name: String = EMPTY_ITEM,
    var description: String? = EMPTY_STRING,
    var detailedDescription: String? = EMPTY_STRING,
    var image: ByteArray? = null,
    var imagePk: Long? = null,
    var videoUri: String? = EMPTY_STRING,
    var videoUrl: String? = EMPTY_STRING,
    var parent: Long? = null
) : Parcelable {
//    companion object {
//        val AUTOGEN_PK = "autogenPk"
//        val NAME = "name"
//        val DESCRIPTION = "description"
//        val DETAILED_DESCRIPTION = "detailedDescription"
//        val IMAGE = "image"
//        val IMAGEPK = "imagePk"
//        val VIDEO_URI = "videoUri"
//        val VIDEO_URL = "videoUrl"
//        val PARENT = "parent"
//        val SIBLINGS = "siblings"
//    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReflexionItem

        if (autogenPk != other.autogenPk) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (detailedDescription != other.detailedDescription) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false
        if (imagePk != other.imagePk) return false
        if (videoUri != other.videoUri) return false
        if (videoUrl != other.videoUrl) return false
        if (parent != other.parent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = autogenPk.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (detailedDescription?.hashCode() ?: 0)
        result = 31 * result + (image?.contentHashCode() ?: 0)
        result = 31 * result + (imagePk?.hashCode() ?: 0)
        result = 31 * result + (videoUri?.hashCode() ?: 0)
        result = 31 * result + (videoUrl?.hashCode() ?: 0)
        result = 31 * result + (parent?.hashCode() ?: 0)

        return result
    }
}