package com.linku.im

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.linku.core.wrapper.Event
import com.linku.core.wrapper.Resource
import com.linku.core.wrapper.eventOf
import com.linku.data.Configurations
import com.linku.data.usecase.*
import com.linku.domain.auth.Authenticator
import com.linku.domain.entity.toComposeTheme
import com.linku.domain.repository.SessionRepository
import com.linku.im.network.ConnectivityObserver
import com.linku.im.screen.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LinkUViewModel @Inject constructor(
    private val messages: MessageUseCases,
    private val sessions: SessionUseCases,
    private val emojis: EmojiUseCases,
    private val applications: ApplicationUseCases,
    private val configurations: Configurations,
    private val conversations: ConversationUseCases,
    private val themes: SettingUseCases.Themes,
    val authenticator: Authenticator,
    connectivityObserver: ConnectivityObserver,
) : BaseViewModel<LinkUState, LinkUEvent>(LinkUState()) {
    init {
        onEvent(LinkUEvent.InitConfig)
        sessions.state()
            .onEach { state ->
                when (state) {
                    SessionRepository.State.Default -> deliverState(Label.NoAuth)
                    SessionRepository.State.Connecting -> deliverState(Label.Connecting)
                    SessionRepository.State.Connected -> {}
                    SessionRepository.State.Subscribing -> deliverState(Label.Subscribing)
                    SessionRepository.State.Subscribed -> deliverState(Label.Default)
                    is SessionRepository.State.Failed -> deliverState(Label.Failed)
                    SessionRepository.State.Lost -> deliverState(Label.Failed)
                }
            }
            .launchIn(viewModelScope)
        connectivityObserver.observe()
            .onEach { state ->
                when (state) {
                    ConnectivityObserver.State.Available -> {
                        initSessionJob?.cancel()
                        initSessionJob = authenticator.observeCurrent
                            .distinctUntilChanged()
                            .onEach { userId ->
                                if (userId != null) onEvent(LinkUEvent.InitSession)
                                else onEvent(LinkUEvent.Disconnect)
                            }
                            .launchIn(viewModelScope)
                    }

                    ConnectivityObserver.State.Unavailable -> {
                    }

                    ConnectivityObserver.State.Losing -> {
                        initSessionJob?.cancel()
                    }

                    ConnectivityObserver.State.Lost -> {
                    }
                }
            }
            .launchIn(viewModelScope)

    }

    override fun onEvent(event: LinkUEvent) {
        when (event) {
            LinkUEvent.InitSession -> initRemoteSession()
            LinkUEvent.InitConfig -> initConfig()
            LinkUEvent.ToggleDarkMode -> {
                val saved = !readable.isDarkMode
                configurations.isDarkMode = saved
                writable = readable.copy(
                    isDarkMode = saved
                )
            }

            LinkUEvent.Disconnect -> {
                viewModelScope.launch {
                    sessions.close()
                }
            }

            is LinkUEvent.OnTheme -> {
                viewModelScope.launch {
                    writable = if (event.isDarkMode) {
                        readable.copy(
                            darkTheme = event.tid.let {
                                themes.findById(it)?.toComposeTheme() ?: readable.darkTheme
                            },
                            isDarkMode = true
                        )
                    } else {
                        readable.copy(
                            lightTheme = event.tid.let {
                                themes.findById(it)?.toComposeTheme() ?: readable.lightTheme
                            },
                            isDarkMode = false
                        )
                    }

                }
            }

            LinkUEvent.Premium -> {
                onMessage(applications.getString(R.string.premium_unavailable))
            }
            is LinkUEvent.OnExperimentMode -> {
                writable = readable.copy(
                    isExperimentMode = event.target
                )
            }
        }
    }

    private var _message = mutableStateOf<Event<String>>(Event.Handled())
    var message by _message
        private set
    public override fun onMessage(message: String?) {
        this.message = eventOf(message ?: return)
    }

    private sealed class Label {
        object Default : Label()
        object Connecting : Label()
        object Failed : Label()
        object Subscribing : Label()
        object SubscribedFailed : Label()
        object NoAuth : Label()
    }

    private fun deliverState(label: Label) {
        writable = readable.copy(
            label = when (label) {
                Label.Default -> {
                    writable = readable.copy(
                        loading = false
                    )
                    null
                }

                Label.Connecting -> {
                    writable = readable.copy(
                        loading = true
                    )
                    applications.getString(R.string.connecting)
                }

                Label.Failed -> applications.getString(R.string.connected_failed)
                Label.Subscribing -> {
                    writable = readable.copy(
                        loading = true
                    )
                    applications.getString(R.string.subscribing)
                }

                Label.SubscribedFailed -> applications.getString(R.string.subscribe_failed)
                Label.NoAuth -> applications.getString(R.string.no_auth)
            }
        )
    }

    private var initSessionJob: Job? = null
    private var fetchConversationsJob: Job? = null
    private var times = 0
    private fun initRemoteSession() {
        configurations.log {
            times++
            onMessage("Init session, times: $times")
        }

        fetchConversationsJob?.cancel()
        fetchConversationsJob = conversations
            .fetchConversations()
            .launchIn(viewModelScope)

        initSessionJob?.cancel()
        initSessionJob = sessions.init(authenticator.currentUID)
            .onEach { resource ->
                when (resource) {
                    Resource.Loading -> {}
                    is Resource.Success -> {
                        sessions.subscribeRemote()
                            .onEach {
                                when (it) {
                                    Resource.Loading -> {}
                                    is Resource.Success -> {
                                        messages.syncingMessages()
                                        deliverState(Label.Default)
                                        writable = readable.copy(
                                            readyForObserveMessages = true
                                        )
                                    }

                                    is Resource.Failure -> {
                                        deliverState(Label.SubscribedFailed)
                                        writable = readable.copy(
                                            readyForObserveMessages = true
                                        )
                                        delay(3000)
                                        configurations.log {
                                            onMessage(it.message)
                                        }
                                        onEvent(LinkUEvent.InitSession)
                                    }
                                }
                            }
                            .launchIn(viewModelScope)
                    }

                    is Resource.Failure -> {
                        deliverState(Label.Failed)
                        delay(3000)
                        configurations.log {
                            onMessage(resource.message)
                        }
                        onEvent(LinkUEvent.InitSession)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun initConfig() {
        fun initEmoji(
            onSuccess: () -> Unit,
            onFailure: (String?) -> Unit
        ) {
            emojis.initialize()
                .onEach { resource ->
                    when (resource) {
                        Resource.Loading -> {}
                        is Resource.Success -> onSuccess()
                        is Resource.Failure -> onFailure(resource.message)
                    }
                }
                .launchIn(viewModelScope)
        }

        fun initTheme(
            onSuccess: () -> Unit,
            onFailure: (String?) -> Unit
        ) {
            themes.installDefaultTheme()
                .onEach { resource ->
                    when (resource) {
                        Resource.Loading -> {}
                        is Resource.Success -> {
                            writable = readable.copy(
                                lightTheme = themes.findById(
                                    configurations.lightTheme
                                )?.toComposeTheme() ?: readable.lightTheme,
                                darkTheme = themes.findById(
                                    configurations.darkTheme
                                )?.toComposeTheme() ?: readable.darkTheme,
                            )
                            onSuccess()
                        }

                        is Resource.Failure -> onFailure(resource.message)
                    }
                }
                .launchIn(viewModelScope)
        }

        val isDarkMode = configurations.isDarkMode
        initEmoji(
            onSuccess = {
                writable = readable.copy(
                    isDarkMode = isDarkMode,
                    isEmojiReady = true
                )
            },
            onFailure = {
                onMessage(it)
                writable = readable.copy(
                    isDarkMode = isDarkMode,
                    isEmojiReady = true
                )
            }
        )
        initTheme(
            onSuccess = {
                writable = readable.copy(
                    isThemeReady = true
                )
            },
            onFailure = {
                onMessage(it)
                writable = readable.copy(
                    isThemeReady = true
                )
            }
        )
    }

}
