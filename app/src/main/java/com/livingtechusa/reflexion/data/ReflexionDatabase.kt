package com.livingtechusa.reflexion.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.livingtechusa.reflexion.data.dao.ReflexionItemDao
import com.livingtechusa.reflexion.data.dao.KeyWordsDao
import com.livingtechusa.reflexion.data.dao.LinkedListDao
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.entities.KeyWords
import com.livingtechusa.reflexion.data.entities.LinkedList

@Database(
    entities =
    [ReflexionItem::class,
        KeyWords::class,
    LinkedList::class],
    version = 1,
    exportSchema = false
)

abstract class ReflexionDatabase : RoomDatabase() {
    abstract fun itemDao(): ReflexionItemDao
    abstract fun keyWordsDao(): KeyWordsDao
    abstract fun linkedListDao(): LinkedListDao
}
