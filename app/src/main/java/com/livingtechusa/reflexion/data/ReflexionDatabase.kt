package com.livingtechusa.reflexion.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.livingtechusa.reflexion.data.dao.BookMarksDao
import com.livingtechusa.reflexion.data.dao.ImagesDao
import com.livingtechusa.reflexion.data.dao.LinkedListDao
import com.livingtechusa.reflexion.data.dao.ReflexionItemDao
import com.livingtechusa.reflexion.data.entities.Bookmarks
import com.livingtechusa.reflexion.data.entities.Image
import com.livingtechusa.reflexion.data.entities.ItemImageAssociativeData
import com.livingtechusa.reflexion.data.entities.ListNode
import com.livingtechusa.reflexion.data.entities.ReflexionItem

@Database(
    entities =
    [ReflexionItem::class,
        Bookmarks::class,
        ListNode::class,
        Image::class,
        ItemImageAssociativeData::class],
    version = 1,
    exportSchema = false
)

@androidx.room.TypeConverters(com.livingtechusa.reflexion.data.entities.TypeConverters::class)

abstract class ReflexionDatabase : RoomDatabase() {
    abstract fun itemDao(): ReflexionItemDao
    abstract fun keyWordsDao(): BookMarksDao
    abstract fun linkedListDao(): LinkedListDao
    abstract fun imagesDao(): ImagesDao
}
