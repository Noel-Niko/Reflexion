package com.livingtechusa.reflexion.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.livingtechusa.reflexion.data.entities.ReflexionItem

@Dao
interface ReflexionItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setReflexionItem(item: ReflexionItem)

    @Query("UPDATE ReflexionItem SET name = :name, description = :description, detailedDescription = :detailedDescription, image = :image, videoUri = :videoUri, videoUrl = :videoUrl, parent = :parent, hasChildren = :hasChildren WHERE autogenPK = :autogenPK")
    suspend fun updateReflexionItem(autogenPK: Long , name: String, description: String?, detailedDescription: String?, image: ByteArray?, videoUri: String?, videoUrl: String?, parent: Long?, hasChildren: Boolean?)

    @Query("Select * FROM ReflexionItem")
    suspend fun getAllReflexionItems(): List<ReflexionItem?>

    @Query("Delete FROM ReflexionItem")
    suspend fun clearALLReflexionItems()

    @Query("Select * FROM ReflexionItem WHERE autogenPK = :autogenPK AND name = :name")
    suspend fun selectReflexionItem(autogenPK: Long, name: String): ReflexionItem?

    @Query("Delete FROM ReflexionItem WHERE autogenPK = :autogenPK AND name = :name")
    suspend fun deleteReflexionItem(autogenPK: Long, name: String)

    @Query("UPDATE ReflexionItem SET name = :newName WHERE autogenPK = :autogenPK AND name = :name")
    suspend fun renameReflexionItem(autogenPK: Long, name: String, newName: String)

    @Query("Select * FROM ReflexionItem WHERE parent =:parent")
    suspend fun selectChildReflexionItems(parent: Long): List<ReflexionItem?>

    @Query("UPDATE ReflexionItem SET parent = :newParent WHERE autogenPK = :autogenPK AND name = :name")
    suspend fun setReflexionItemParent(autogenPK: Long, name: String, newParent: Long)

    @Query("Select * FROM ReflexionItem WHERE name =:name")
    suspend fun selectReflexionItemByName(name: String): ReflexionItem
}