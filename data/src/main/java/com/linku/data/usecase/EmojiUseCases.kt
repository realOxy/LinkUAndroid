package com.linku.data.usecase

import com.linku.core.wrapper.Resource
import com.linku.domain.bean.Emoji
import com.linku.domain.service.system.EmojiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class EmojiUseCases @Inject constructor(
    val initialize: InitializeUseCase,
    val getAll: GetAllUseCase
)

data class InitializeUseCase @Inject constructor(
    private val emojiService: EmojiService
) {
    operator fun invoke(): Flow<Resource<Unit>> = emojiService.initialize()
}

data class GetAllUseCase @Inject constructor(
    private val emojiService: EmojiService
) {
    operator fun invoke(): List<Emoji> = emojiService.emojis()
}
