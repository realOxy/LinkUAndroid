package com.linku.data.repository

import com.linku.domain.Resource
import com.linku.domain.emitResource
import com.linku.domain.entity.Conversation
import com.linku.domain.entity.Message
import com.linku.domain.entity.toConversation
import com.linku.domain.repository.ConversationRepository
import com.linku.domain.resourceFlow
import com.linku.domain.room.dao.ConversationDao
import com.linku.domain.room.dao.MessageDao
import com.linku.domain.service.ChatService
import com.linku.domain.service.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ConversationRepositoryImpl(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val chatService: ChatService,
    private val userService: UserService
) : ConversationRepository {
    override fun observeConversation(cid: Int): Flow<Conversation> {
        return try {
            conversationDao.observeConversation(cid)
        } catch (e: Exception) {
            e.printStackTrace()
            flow { }
        }
    }

    override fun observeConversations(): Flow<List<Conversation>> {
        return try {
            conversationDao.observeConversations()
        } catch (e: Exception) {
            e.printStackTrace()
            flow { }
        }
    }

    override fun fetchConversation(cid: Int): Flow<Resource<Unit>> = resourceFlow {
        chatService.getById(cid)
            .handle { conversation ->
                // TODO save different type conversations
                if (conversationDao.getById(conversation.id) == null) {
                    conversationDao.insert(conversation.toConversation())
                }
                emitResource(Unit)
            }
            .catch(::emitResource)
    }

    override fun fetchConversations(): Flow<Resource<Unit>> = resourceFlow {
        chatService.getConversationsBySelf()
            .handle { conversations ->
                conversations.forEach { conversationDao.insert(it.toConversation()) }
                emitResource(Unit)
            }
            .catch(::emitResource)
    }

    override fun observeLatestMessages(cid: Int): Flow<Message> {
        return messageDao.getLatestMessageByCid(cid).filterNotNull().map { it.toReadable() }
    }

    override fun queryConversations(
        name: String?,
        description: String?
    ): Flow<Resource<List<Conversation>>> = resourceFlow {
        chatService.queryConversations(name, description)
            .handle { conversations -> emitResource(conversations.map { it.toConversation() }) }
            .catch(::emitResource)
    }
}