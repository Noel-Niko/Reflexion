package com.livingtechusa.reflexion.data.models

import com.livingtechusa.reflexion.data.entities.ReflexionItem
import okio.ByteString.Companion.toByteString

data class ReflexionItemAsJson(
    var autogenPk: String,
    var name: String,
    var description: String? = null,
    var detailedDescription: String? = null,
    var image: ByteArray? = null,
    var imagePk: String? = null,
    var videoUri: String? = null,
    var videoUrl: String? = null,
    var parent: String? = null
) {
    fun toReflexionItem(): ReflexionItem {
        return ReflexionItem(
            autogenPk = autogenPk.toLong(),
            name = name,
            description = description,
            detailedDescription = detailedDescription,
            image = image?.toByteString()?.toByteArray(),
            imagePk = imagePk?.toLong(),
            videoUri = videoUri,
            videoUrl =videoUrl,
            parent = null//parent?.toLong()
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReflexionItemAsJson

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