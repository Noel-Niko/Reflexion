package com.livingtechusa.reflexion.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.models.AbridgedReflexionItem

@Dao
interface ReflexionItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setReflexionItem(item: ReflexionItem)

    @Query("UPDATE ReflexionItem SET name = :name, description = :description, detailedDescription = :detailedDescription, image = :image, videoUri = :videoUri, videoUrl = :videoUrl, parent = :parent WHERE autogenPK = :autogenPK")
    suspend fun updateReflexionItem(autogenPK: Long , name: String, description: String?, detailedDescription: String?, image: ByteArray?, videoUri: String?, videoUrl: String?, parent: Long?)

    @Query("Select * FROM ReflexionItem order by name ASC")
    suspend fun getAllReflexionItems(): List<ReflexionItem?>

    @Query("Delete FROM ReflexionItem")
    suspend fun clearALLReflexionItems()

    @Query("Select * FROM ReflexionItem WHERE parent IS NULL ORDER BY name ASC")
    suspend fun getReflexionItemTopics(): List<ReflexionItem?>

    @Query("Select * FROM ReflexionItem WHERE autogenPK = :autogenPK")
    suspend fun selectReflexionItem(autogenPK: Long): ReflexionItem?

    @Query("Delete FROM ReflexionItem WHERE autogenPK = :autogenPK AND name = :name")
    suspend fun deleteReflexionItem(autogenPK: Long, name: String)

    @Query("UPDATE ReflexionItem SET name = :newName WHERE autogenPK = :autogenPK AND name = :name")
    suspend fun renameReflexionItem(autogenPK: Long, name: String, newName: String)

    @Query("Select * FROM ReflexionItem WHERE parent =:parent order by name")
    suspend fun selectChildReflexionItems(parent: Long): List<ReflexionItem?>
    @Query("SELECT * FROM ReflexionItem WHERE videoUri = :uri Limit 1")
    suspend fun selectItemByUri(uri: String): ReflexionItem?

    @Query("Select autogenPK, name, parent FROM ReflexionItem WHERE parent IS NULL order by name ASC")
    suspend fun getAbridgedReflexionItemTopics(): List<AbridgedReflexionItem?>
    @Query("Select autogenPK, name, parent FROM ReflexionItem WHERE parent =:parent order by name ASC")
    suspend fun selectAbridgedReflexionItemDataByParentPk(parent: Long): List<AbridgedReflexionItem?>

    @Query("Select autogenPK, name, parent FROM ReflexionItem WHERE parent =:pk")
    suspend fun selectSingleAbridgedReflexionItemDataByParentPk(pk: Long): AbridgedReflexionItem

    @Query("Select autogenPK, name, parent FROM ReflexionItem WHERE autogenPK =:pk")
    suspend fun selectSingleAbridgedReflexionItem(pk: Long): AbridgedReflexionItem

    @Query("UPDATE ReflexionItem SET parent = :newParent WHERE autogenPK = :autogenPK AND name = :name")
    suspend fun setReflexionItemParent(autogenPK: Long, name: String, newParent: Long)

    @Query("Select * FROM ReflexionItem WHERE name =:name")
    suspend fun selectReflexionItemByName(name: String): ReflexionItem

    @Query("Select * FROM ReflexionItem WHERE name LIKE :search || '%' order by name ASC")
    suspend fun getAllItemsContainingString(search: String): List<ReflexionItem?>

    @Query("Select * FROM ReflexionItem WHERE parent IS NULL AND name LIKE :search || '%' order by name ASC")
    suspend fun getAllTopicsContainingString(search: String): List<ReflexionItem?>
    @Query("Select * FROM ReflexionItem WHERE parent =:pk AND name LIKE :search || '%' order by name ASC")
    suspend fun selectChildrenContainingString(pk: Long, search: String?): List<ReflexionItem?>
    @Query("SELECT * FROM ReflexionItem WHERE parent = :parent AND autogenPK IS NOT :pk order by name ASC")
    suspend fun selectSiblings(pk: Long, parent: Long): List<ReflexionItem?>

    @Query("SELECT * FROM ReflexionItem WHERE parent = :parent order by name ASC")
    suspend fun selectAllSiblings(parent: Long): List<ReflexionItem?>
    @Query("SELECT parent FROM ReflexionItem WHERE autogenPK = :itemPk")
    suspend fun getParent(itemPk: Long): Long?
    @Query("SELECT image FROM ReflexionItem WHERE autogenPK = :itemPk")
    suspend fun selectImage(itemPk: Long): ByteArray?
}