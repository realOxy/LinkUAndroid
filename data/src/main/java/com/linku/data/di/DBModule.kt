package com.linku.data.di

import android.content.Context
import androidx.room.Room
import com.linku.domain.Constants
import com.linku.domain.room.LinkUDatabase
import com.linku.domain.room.dao.ConversationDao
import com.linku.domain.room.dao.MessageDao
import com.linku.domain.room.dao.ThemeDao
import com.linku.domain.room.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): LinkUDatabase = Room.databaseBuilder(
        context,
        LinkUDatabase::class.java,
        Constants.DB_NAME
    ).build()

    @Provides
    @Singleton
    fun provideUserDao(database: LinkUDatabase): UserDao = database.userDao()

    @Provides
    @Singleton
    fun provideConversationDao(database: LinkUDatabase): ConversationDao =
        database.conversationDao()

    @Provides
    @Singleton
    fun provideMessageDao(database: LinkUDatabase): MessageDao = database.messageDao()

    @Provides
    @Singleton
    fun provideThemeDao(database: LinkUDatabase): ThemeDao = database.themeDao()
}
