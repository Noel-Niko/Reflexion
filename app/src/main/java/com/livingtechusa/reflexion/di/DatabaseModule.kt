package com.livingtechusa.gotjokes.di

import android.content.Context
import androidx.room.Room
import com.livingtechusa.reflexion.data.ReflexionDatabase
import com.livingtechusa.reflexion.data.dao.ReflexionItemDao
import com.livingtechusa.reflexion.data.dao.KeyWordsDao
import com.livingtechusa.reflexion.data.dao.LinkedListDao
import com.livingtechusa.reflexion.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideItemDao(reflexionDatabase: ReflexionDatabase): ReflexionItemDao {
        return reflexionDatabase.itemDao()
    }
    @Provides
    fun provideKeyWordsDao(reflexionDatabase: ReflexionDatabase): KeyWordsDao {
        return reflexionDatabase.keyWordsDao()
    }

    @Provides
    fun provideLinkedListDao(reflexionDatabase: ReflexionDatabase): LinkedListDao {
        return reflexionDatabase.linkedListDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context):
        ReflexionDatabase {
        return Room.databaseBuilder(
            appContext,
            ReflexionDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}