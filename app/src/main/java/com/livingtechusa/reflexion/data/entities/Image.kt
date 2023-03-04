package com.livingtechusa.reflexion.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.checkerframework.common.aliasing.qual.Unique

@Entity(
    tableName = "Image",
    indices = [Index(value = ["image"], unique = true)]
)
data class Image(
    @PrimaryKey(autoGenerate = true)
    val imagePk: Long,
    var image: ByteArray = ByteArray(0)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (imagePk != other.imagePk) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = imagePk.hashCode()
        result = 31 * result + (image?.contentHashCode() ?: 0)
        return result
    }
}
