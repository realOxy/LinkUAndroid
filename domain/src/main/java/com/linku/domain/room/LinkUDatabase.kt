package com.linku.domain.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.linku.domain.entity.Conversation
import com.linku.domain.entity.Message
import com.linku.domain.entity.Theme
import com.linku.domain.entity.User
import com.linku.domain.room.converter.IntList
import com.linku.domain.room.dao.ConversationDao
import com.linku.domain.room.dao.MessageDao
import com.linku.domain.room.dao.ThemeDao
import com.linku.domain.room.dao.UserDao

@Database(
    entities = [
        User::class,
        Message::class,
        Conversation::class,
        Theme::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    Conversation.Type.Converter::class,
    Message.Type.Converter::class,
    IntList::class
)
abstract class LinkUDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
    abstract fun conversationDao(): ConversationDao
    abstract fun themeDao(): ThemeDao
}
