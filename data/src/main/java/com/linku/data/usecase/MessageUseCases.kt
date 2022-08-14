package com.linku.data.usecase

import android.net.Uri
import com.linku.domain.Resource
import com.linku.domain.Strategy
import com.linku.domain.entity.Message
import com.linku.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class MessageUseCases @Inject constructor(
    val initSession: InitSessionUseCase,
    val observeAllMessages: ObserveAllMessagesUseCase,
    val observeMessages: ObserveMessagesUseCase,
    val closeSession: CloseSessionUseCase,
    val textMessage: TextMessageUseCase,
    val imageMessage: ImageMessageUseCase,
    val graphicsMessage: GraphicsMessageUseCase,
    val getMessage: GetMessageUseCase
)

data class GetMessageUseCase(
    val repository: MessageRepository
) {
    suspend operator fun invoke(
        mid: Int,
        strategy: Strategy
    ): Message? = repository.getMessageById(mid, strategy)
}

data class TextMessageUseCase(
    val repository: MessageRepository
) {
    suspend operator fun invoke(
        cid: Int,
        text: String,
        reply: Int? = null
    ): Flow<Resource<Unit>> = repository.sendTextMessage(cid, text, reply)
}

data class ImageMessageUseCase(
    val messageRepository: MessageRepository
) {
    operator fun invoke(
        cid: Int,
        uri: Uri,
        reply: Int? = null
    ): Flow<Resource<Unit>> = messageRepository.sendImageMessage(cid, uri, reply)
}

data class GraphicsMessageUseCase(
    val messageRepository: MessageRepository
) {
    operator fun invoke(
        cid: Int,
        text: String,
        uri: Uri,
        reply: Int? = null
    ): Flow<Resource<Unit>> = messageRepository.sendGraphicsMessage(cid, text, uri, reply)
}

data class InitSessionUseCase(
    private val repository: MessageRepository
) {
    operator fun invoke(uid: Int?): Flow<Resource<Unit>> = run {
        repository.initSession(uid)
    }
}

data class ObserveAllMessagesUseCase(
    private val repository: MessageRepository
) {
    operator fun invoke(): Flow<List<Message>> {
        return repository.incoming()
    }
}

data class ObserveMessagesUseCase(
    private val repository: MessageRepository
) {
    operator fun invoke(cid: Int): Flow<List<Message>> {
        return repository.incoming(cid)
    }
}

data class CloseSessionUseCase(
    private val repository: MessageRepository
) {
    suspend operator fun invoke() {
        repository.closeSession()
    }
}
