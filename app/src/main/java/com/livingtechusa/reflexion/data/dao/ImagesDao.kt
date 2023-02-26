package com.livingtechusa.reflexion.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ImagesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ByteArray): Long

    @Query("Select image FROM Image WHERE imagePk = :imagePk")
    suspend fun selectImageByPK(imagePk: Long): ByteArray

    @Query("Select image FROM Image WHERE image =:byteArray")
    suspend fun selectImageByByteArray(byteArray: ByteArray): ByteArray

    @Query("Select imagePk FROM Image WHERE image =:byteArray")
    suspend fun selectImagePKByByteArray(byteArray: ByteArray): Long?

    @Query("UPDATE Image SET useCount = useCount +1 WHERE imagePk = :imagePk")
    suspend fun setIncreaseCount(imagePk: Long)

    @Query("UPDATE Image SET useCount = useCount -1 WHERE imagePk = :imagePk")
    suspend fun setDecreaseCount(imagePk: Long)

    @Query("DELETE from Image WHERE imagePk = :imagePk AND useCount = 0")
    suspend fun setDeleteImageIfUnused(imagePk: Long)

}
