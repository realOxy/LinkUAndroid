package com.linku.im.ui.components.item

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.linku.core.extension.ifTrue
import com.linku.domain.bean.ui.ContactUI
import com.linku.domain.bean.ui.ConversationUI
import com.linku.domain.entity.Conversation
import com.linku.im.ktx.ui.graphics.times
import com.linku.im.ktx.ui.intervalClickable
import com.linku.im.ui.theme.LocalSpacing
import com.linku.im.ui.theme.LocalTheme

@Composable
fun ConversationItem(
    modifier: Modifier = Modifier,
    conversation: Conversation? = null,
    onClick: () -> Unit = {}
) {
    val shimmerColor = LocalTheme.current.onSurface * 0.3f
    val onShimmerColor = Color.White
    val shimmerAnimationSpec: InfiniteRepeatableSpec<Float> by lazy {
        infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                delayMillis = 400
            ),
            repeatMode = RepeatMode.Restart
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .intervalClickable(
                enabled = conversation != null,
                onClick = onClick
            )
            .padding(
                horizontal = LocalSpacing.current.medium,
                vertical = LocalSpacing.current.extraSmall
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleHeadPicture(
            model = conversation?.avatar,
            placeholder = { TextImage(text = conversation?.name.orEmpty()) }
        )
        Spacer(modifier = Modifier.width(LocalSpacing.current.medium))
        Column(
            modifier = Modifier
                .padding(
                    end = LocalSpacing.current.medium,
                    top = LocalSpacing.current.small,
                    bottom = LocalSpacing.current.small
                )
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = conversation?.name.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                color = LocalTheme.current.onSurface,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = conversation == null,
                        color = shimmerColor,
                        shape = RoundedCornerShape(4.dp),
                        highlight = PlaceholderHighlight.shimmer(
                            highlightColor = onShimmerColor,
                            animationSpec = shimmerAnimationSpec,
                        )
                    ),
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = conversation?.description.orEmpty(),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .placeholder(
                            visible = (conversation == null),
                            color = shimmerColor,
                            shape = RoundedCornerShape(4.dp),
                            highlight = PlaceholderHighlight.shimmer(
                                highlightColor = onShimmerColor,
                                animationSpec = shimmerAnimationSpec
                            ),
                        )
                        .fillMaxWidth(),
                    overflow = TextOverflow.Ellipsis
                )
            }

        }
        Spacer(modifier = Modifier.width(8.dp))
    }
}


@Composable
fun PinnedConversationItem(
    modifier: Modifier = Modifier,
    conversation: ConversationUI? = null,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    val unreadCount: Int = conversation?.unreadCount ?: 0
    val pinned: Boolean = conversation?.pinned ?: false
    val shimmerColor = LocalTheme.current.divider * 0.3f
    val onShimmerColor = Color.White
    val shimmerAnimationSpec: InfiniteRepeatableSpec<Float> by lazy {
        infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                delayMillis = 400
            ),
            repeatMode = RepeatMode.Restart
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .let {
                pinned.ifTrue {
                    it.background(LocalTheme.current.surface * 0.8f)
                } ?: it
            }
            .intervalClickable(
                enabled = conversation != null,
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(
                horizontal = LocalSpacing.current.medium,
                vertical = LocalSpacing.current.extraSmall
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleHeadPicture(
            model = conversation?.image,
            placeholder = { TextImage(conversation?.name.orEmpty()) }
        )
        Spacer(modifier = Modifier.width(LocalSpacing.current.medium))
        Column(
            modifier = Modifier
                .padding(
                    end = LocalSpacing.current.medium,
                    top = LocalSpacing.current.small,
                    bottom = LocalSpacing.current.small
                )
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = conversation?.name.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                color = LocalTheme.current.onSurface,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = conversation == null,
                        color = shimmerColor,
                        shape = RoundedCornerShape(4.dp),
                        highlight = PlaceholderHighlight.shimmer(
                            highlightColor = onShimmerColor,
                            animationSpec = shimmerAnimationSpec,
                        )
                    ),
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            CompositionLocalProvider(LocalContentColor provides LocalTheme.current.onSurface * 0.8f) {
                Text(
                    text = conversation?.content.orEmpty(),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .placeholder(
                            visible = (conversation == null),
                            color = shimmerColor,
                            shape = RoundedCornerShape(4.dp),
                            highlight = PlaceholderHighlight.shimmer(
                                highlightColor = onShimmerColor,
                                animationSpec = shimmerAnimationSpec
                            ),
                        )
                        .fillMaxWidth(),
                    overflow = TextOverflow.Ellipsis
                )
            }

        }
        Spacer(modifier = Modifier.width(LocalSpacing.current.small))
        when {
            (unreadCount != 0) -> {
                Surface(
                    shape = CircleShape,
                    color = LocalTheme.current.primary
                ) {
                    Text(
                        text = unreadCount.toString(),
                        color = LocalTheme.current.onPrimary,
                        modifier = Modifier.padding(horizontal = LocalSpacing.current.small),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        }
        Crossfade(pinned) { pinned ->
            if (pinned) {
                Surface(
                    shape = CircleShape,
                    color = LocalTheme.current.primary,
                    modifier = Modifier.size(LocalSpacing.current.large)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = "",
                        tint = LocalTheme.current.onPrimary,
                        modifier = Modifier.padding(LocalSpacing.current.extraSmall)
                    )
                }
            }
        }
    }
}

@Composable
fun PinnedContractsItem(
    modifier: Modifier = Modifier,
    contact: ContactUI? = null,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    val unreadCount: Int = contact?.unreadCount ?: 0
    val pinned: Boolean = contact?.pinned ?: false
    val shimmerColor = LocalTheme.current.divider * 0.3f
    val onShimmerColor = Color.White
    val shimmerAnimationSpec: InfiniteRepeatableSpec<Float> by lazy {
        infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                delayMillis = 400
            ),
            repeatMode = RepeatMode.Restart
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .let {
                pinned.ifTrue {
                    it.background(LocalTheme.current.surface * 0.8f)
                } ?: it
            }
            .intervalClickable(
                enabled = contact != null,
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(
                horizontal = LocalSpacing.current.medium,
                vertical = LocalSpacing.current.extraSmall
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleHeadPicture(
            model = contact?.image,
            placeholder = { TextImage(contact?.username.orEmpty()) }
        )
        Spacer(modifier = Modifier.width(LocalSpacing.current.medium))
        Column(
            modifier = Modifier
                .padding(
                    end = LocalSpacing.current.medium,
                    top = LocalSpacing.current.small,
                    bottom = LocalSpacing.current.small
                )
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = contact?.username.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                color = LocalTheme.current.onSurface,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = contact == null,
                        color = shimmerColor,
                        shape = RoundedCornerShape(4.dp),
                        highlight = PlaceholderHighlight.shimmer(
                            highlightColor = onShimmerColor,
                            animationSpec = shimmerAnimationSpec,
                        )
                    ),
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            CompositionLocalProvider(LocalContentColor provides LocalTheme.current.onSurface * 0.8f) {
                Text(
                    text = contact?.content.orEmpty(),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .placeholder(
                            visible = (contact == null),
                            color = shimmerColor,
                            shape = RoundedCornerShape(4.dp),
                            highlight = PlaceholderHighlight.shimmer(
                                highlightColor = onShimmerColor,
                                animationSpec = shimmerAnimationSpec
                            ),
                        )
                        .fillMaxWidth(),
                    overflow = TextOverflow.Ellipsis
                )
            }

        }
        Spacer(modifier = Modifier.width(LocalSpacing.current.small))
        when {
            (unreadCount != 0) -> {
                Surface(
                    shape = CircleShape,
                    color = LocalTheme.current.primary
                ) {
                    Text(
                        text = unreadCount.toString(),
                        color = LocalTheme.current.onPrimary,
                        modifier = Modifier.padding(horizontal = LocalSpacing.current.small),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        }
        Crossfade(pinned) { pinned ->
            if (pinned) {
                Surface(
                    shape = CircleShape,
                    color = LocalTheme.current.primary,
                    modifier = Modifier.size(LocalSpacing.current.large)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = "",
                        tint = LocalTheme.current.onPrimary,
                        modifier = Modifier.padding(LocalSpacing.current.extraSmall)
                    )
                }
            }
        }
    }
}
