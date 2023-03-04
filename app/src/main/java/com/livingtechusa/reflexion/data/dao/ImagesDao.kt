package com.livingtechusa.reflexion.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.livingtechusa.reflexion.data.entities.Image
import com.livingtechusa.reflexion.data.entities.ItemImageAssociativeData


@Dao
interface ImagesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: Image): Long

    @Query("Select image FROM Image WHERE imagePk = :imagePk")
    suspend fun selectImageByPK(imagePk: Long): ByteArray

    @Query("Select COUNT(*) FROM Image WHERE imagePk = :imagePk")
    suspend fun countImagePkUses(imagePk: Long): Int
    @Query("Select image FROM Image WHERE image =:byteArray")
    suspend fun selectImageByByteArray(byteArray: ByteArray): ByteArray

    @Query("Select imagePk FROM Image WHERE image =:byteArray")
    suspend fun selectImagePKByByteArray(byteArray: ByteArray): Long?


    @Query("DELETE from Image WHERE imagePk =:imagePk")
    suspend fun deleteImage(imagePk: Long)

    // Associative Table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImageAssociation(association: ItemImageAssociativeData): Long

    @Query("SELECT COUNT(*)  from ItemImageAssociativeData WHERE imagePk = :imagePk")
    suspend fun getAssociationUseCount(imagePk: Long): Int

    @Query("DELETE from ItemImageAssociativeData WHERE itemPk IS NULL")
    suspend fun deleteUnusedAssociations()

    @Query("DELETE from ItemImageAssociativeData WHERE itemPk = :itemPk")
    suspend fun removeImageAssociation(itemPk: Long)

    @Query("DELETE FROM Image WHERE Image.imagePk NOT IN (SELECT imagePk FROM ItemImageAssociativeData)")
    suspend fun deleteUnusedImages()
}
