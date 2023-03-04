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
    suspend fun setReflexionItem(item: ReflexionItem): Long

    @Query("UPDATE ReflexionItem SET name = :name, description = :description, detailedDescription = :detailedDescription, imagePk = :imagePk, videoUri = :videoUri, videoUrl = :videoUrl, parent = :parent WHERE autogenPk = :autogenPk")
    suspend fun updateReflexionItem(autogenPk: Long , name: String, description: String?, detailedDescription: String?, imagePk: Long?, videoUri: String?, videoUrl: String?, parent: Long?)

    @Query("Select ReflexionItem.autogenPk, ReflexionItem.name, ReflexionItem.description, ReflexionItem.detailedDescription, ReflexionItem.imagePk, ReflexionItem.videoUri, ReflexionItem.videoUrl, ReflexionItem.parent, Image.image FROM ReflexionItem LEFT JOIN Image ON ReflexionItem.imagePk = Image.imagePk order by name ASC")
    suspend fun getAllReflexionItems(): List<ReflexionItem?>

    @Query("Select ReflexionItem.autogenPk, ReflexionItem.name, ReflexionItem.description, ReflexionItem.detailedDescription, ReflexionItem.imagePk, ReflexionItem.videoUri, ReflexionItem.videoUrl, ReflexionItem.parent, Image.image  FROM ReflexionItem LEFT JOIN Image ON ReflexionItem.imagePk = Image.imagePk WHERE parent IS NULL ORDER BY name ASC")
    suspend fun getReflexionItemTopics(): List<ReflexionItem?>

    @Query("Select ReflexionItem.autogenPk as autogenPk, ReflexionItem.name as name, ReflexionItem.description as description, ReflexionItem.detailedDescription as detailedDescription, ReflexionItem.imagePk as imagePk, ReflexionItem.videoUri as videoUri, ReflexionItem.videoUrl as videoUrl, ReflexionItem.parent as parent, Image.image as image FROM ReflexionItem LEFT JOIN Image ON ReflexionItem.imagePk = Image.imagePk WHERE autogenPk = :autogenPk")
    suspend fun selectReflexionItem(autogenPk: Long): ReflexionItem?

    @Query("Delete FROM ReflexionItem WHERE autogenPk = :autogenPk AND name = :name")
    suspend fun deleteReflexionItem(autogenPk: Long, name: String)

    @Query("UPDATE ReflexionItem SET name = :newName WHERE autogenPk = :autogenPk AND name = :name")
    suspend fun renameReflexionItem(autogenPk: Long, name: String, newName: String)

    @Query("Select ReflexionItem.autogenPk, ReflexionItem.name, ReflexionItem.description, ReflexionItem.detailedDescription, ReflexionItem.imagePk, ReflexionItem.videoUri, ReflexionItem.videoUrl, ReflexionItem.parent, Image.image FROM ReflexionItem LEFT JOIN Image ON ReflexionItem.imagePk = Image.imagePk WHERE parent =:parent order by name")
    suspend fun selectChildReflexionItems(parent: Long): List<ReflexionItem?>
    @Query("SELECT ReflexionItem.autogenPk, ReflexionItem.name, ReflexionItem.description, ReflexionItem.detailedDescription, ReflexionItem.imagePk, ReflexionItem.videoUri, ReflexionItem.videoUrl, ReflexionItem.parent, Image.image FROM ReflexionItem LEFT JOIN Image ON ReflexionItem.imagePk = Image.imagePk WHERE videoUri = :uri Limit 1")
    suspend fun selectItemByUri(uri: String): ReflexionItem?

    @Query("Select autogenPk, name, parent FROM ReflexionItem WHERE parent IS NULL order by name ASC")
    suspend fun getAbridgedReflexionItemTopics(): List<AbridgedReflexionItem?>
    @Query("Select autogenPk, name, parent FROM ReflexionItem WHERE parent =:parent order by name ASC")
    suspend fun selectAbridgedReflexionItemDataByParentPk(parent: Long): List<AbridgedReflexionItem?>

    @Query("Select autogenPk, name, parent FROM ReflexionItem WHERE parent =:pk")
    suspend fun selectSingleAbridgedReflexionItemDataByParentPk(pk: Long): AbridgedReflexionItem

    @Query("Select autogenPk, name, parent FROM ReflexionItem WHERE autogenPk =:pk")
    suspend fun selectSingleAbridgedReflexionItem(pk: Long): AbridgedReflexionItem?

    @Query("UPDATE ReflexionItem SET parent = :newParent WHERE autogenPk = :autogenPk AND name = :name")
    suspend fun setReflexionItemParent(autogenPk: Long, name: String, newParent: Long)

    @Query("Select ReflexionItem.autogenPk, ReflexionItem.name, ReflexionItem.description, ReflexionItem.detailedDescription, ReflexionItem.imagePk, ReflexionItem.videoUri, ReflexionItem.videoUrl, ReflexionItem.parent, Image.image FROM ReflexionItem LEFT JOIN Image ON ReflexionItem.imagePk = Image.imagePk WHERE name =:name")
    suspend fun selectReflexionItemByName(name: String): ReflexionItem

    @Query("Select ReflexionItem.autogenPk, ReflexionItem.name, ReflexionItem.description, ReflexionItem.detailedDescription, ReflexionItem.imagePk, ReflexionItem.videoUri, ReflexionItem.videoUrl, ReflexionItem.parent, Image.image FROM ReflexionItem LEFT JOIN Image ON ReflexionItem.imagePk = Image.imagePk WHERE name LIKE :search || '%' order by name ASC")
    suspend fun getAllItemsContainingString(search: String): List<ReflexionItem?>

    @Query("Select ReflexionItem.autogenPk, ReflexionItem.name, ReflexionItem.description, ReflexionItem.detailedDescription, ReflexionItem.imagePk, ReflexionItem.videoUri, ReflexionItem.videoUrl, ReflexionItem.parent, Image.image FROM ReflexionItem LEFT JOIN Image ON ReflexionItem.imagePk = Image.imagePk WHERE parent IS NULL AND name LIKE :search || '%' order by name ASC")
    suspend fun getAllTopicsContainingString(search: String): List<ReflexionItem?>
    @Query("Select ReflexionItem.autogenPk, ReflexionItem.name, ReflexionItem.description, ReflexionItem.detailedDescription, ReflexionItem.imagePk, ReflexionItem.videoUri, ReflexionItem.videoUrl, ReflexionItem.parent, Image.image FROM ReflexionItem LEFT JOIN Image ON ReflexionItem.imagePk = Image.imagePk WHERE parent =:pk AND name LIKE :search || '%' order by name ASC")
    suspend fun selectChildrenContainingString(pk: Long, search: String?): List<ReflexionItem?>
    @Query("SELECT ReflexionItem.autogenPk, ReflexionItem.name, ReflexionItem.description, ReflexionItem.detailedDescription, ReflexionItem.imagePk, ReflexionItem.videoUri, ReflexionItem.videoUrl, ReflexionItem.parent, Image.image FROM ReflexionItem LEFT JOIN Image ON ReflexionItem.imagePk = Image.imagePk WHERE parent = :parent AND autogenPk IS NOT :pk order by name ASC")
    suspend fun selectSiblings(pk: Long, parent: Long): List<ReflexionItem?>

    @Query("SELECT ReflexionItem.autogenPk, ReflexionItem.name, ReflexionItem.description, ReflexionItem.detailedDescription, ReflexionItem.imagePk, ReflexionItem.videoUri, ReflexionItem.videoUrl, ReflexionItem.parent, Image.image FROM ReflexionItem LEFT JOIN Image ON ReflexionItem.imagePk = Image.imagePk WHERE parent = :parent order by name ASC")
    suspend fun selectAllSiblings(parent: Long): List<ReflexionItem?>
    @Query("SELECT parent FROM ReflexionItem WHERE autogenPk = :itemPk")
    suspend fun getParent(itemPk: Long): Long?
    @Query("SELECT image FROM Image WHERE imagePk = :imagePk")
    suspend fun selectImage(imagePk: Long): ByteArray?
}