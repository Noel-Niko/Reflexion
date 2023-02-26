package com.livingtechusa.reflexion.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.livingtechusa.reflexion.data.dao.ReflexionItemDao
import com.livingtechusa.reflexion.data.dao.BookMarksDao
import com.livingtechusa.reflexion.data.dao.ImagesDao
import com.livingtechusa.reflexion.data.dao.LinkedListDao
import com.livingtechusa.reflexion.data.entities.Converters
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.entities.Bookmarks
import com.livingtechusa.reflexion.data.entities.ListNode

@Database(
    entities =
    [ReflexionItem::class,
        Bookmarks::class,
    ListNode::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(Converters::class)

abstract class ReflexionDatabase : RoomDatabase() {
    abstract fun itemDao(): ReflexionItemDao
    abstract fun keyWordsDao(): BookMarksDao
    abstract fun linkedListDao(): LinkedListDao
    abstract fun imagesDao(): ImagesDao
}
