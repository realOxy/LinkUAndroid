package com.linku.im.screen.chat.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.linku.im.screen.chat.ChatMode
import com.linku.im.ui.components.button.MaterialIconButton
import com.linku.im.ui.theme.LocalSpacing
import com.linku.im.ui.theme.LocalTheme

@Composable
fun ChatTopBar(
    modeProvider: () -> ChatMode,
    title: String,
    subTitle: String,
    onClick: (ChatMode) -> Unit,
    onNavClick: (ChatMode) -> Unit,
    introduce: String,
    modifier: Modifier = Modifier,
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp
) {
    val duration = 400
    val containerColor by animateColorAsState(
        if (modeProvider() is ChatMode.ChannelDetail) LocalTheme.current.secondaryTopBar
        else LocalTheme.current.topBar
    )
    val contentColor by animateColorAsState(
        when (modeProvider()) {
            is ChatMode.ChannelDetail -> LocalTheme.current.onSecondaryTopBar
            is ChatMode.Messages -> LocalTheme.current.onTopBar
            is ChatMode.ImageDetail -> LocalTheme.current.onTopBar
            else -> Color.Transparent
        }
    )
    Surface(
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawWithContent {
                    drawRect(containerColor)
                    drawContent()
                }
                .clickable { onClick(modeProvider()) }
        ) {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            val height by animateDpAsState(
                when (modeProvider()) {
                    is ChatMode.MemberDetail -> 0.dp
                    is ChatMode.ChannelDetail -> Dp.Unspecified
                    else -> Dp.Unspecified
                }
            )
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = LocalSpacing.current.small)
            ) {
                val (iconRef, titleRef, introduceRef, subTitleRef) = createRefs()
                MaterialIconButton(
                    icon = Icons.Default.ArrowBack,
                    onClick = { onNavClick(modeProvider()) },
                    tint = contentColor,
                    modifier = Modifier
                        .constrainAs(iconRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                        .height(height)
                )
                val textHorizontallyBias by animateFloatAsState(
                    when (modeProvider()) {
                        is ChatMode.ChannelDetail -> 0f
                        else -> 0.5f
                    },
                    animationSpec = tween(duration, easing = LinearEasing)
                )
                val textVerticallyBias by animateFloatAsState(
                    when (modeProvider()) {
                        is ChatMode.ChannelDetail -> 1f
                        else -> 0.5f
                    },
                    animationSpec = tween(duration, easing = LinearEasing)
                )
                val textFontSize by animateFloatAsState(
                    when (modeProvider()) {
                        is ChatMode.ChannelDetail -> 24f
                        else -> 16f
                    },
                    animationSpec = tween(duration, easing = LinearEasing)
                )

                val titleAlpha by animateFloatAsState(
                    when (modeProvider()) {
                        else -> 1f
                    }
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    fontSize = textFontSize.sp,
                    maxLines = 1,
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = titleAlpha
                        }
                        .padding(
                            horizontal = LocalSpacing.current.medium,
                            vertical = LocalSpacing.current.extraSmall
                        )
                        .constrainAs(titleRef) {
                            centerHorizontallyTo(parent, textHorizontallyBias)
                            centerVerticallyTo(parent, textVerticallyBias)
                        }
                )

                val subtextFontSize by animateFloatAsState(
                    when (modeProvider()) {
                        is ChatMode.ChannelDetail -> 14f
                        else -> 10f
                    },
                    animationSpec = tween(duration, easing = LinearEasing)
                )
                val subTitleAlpha by animateFloatAsState(
                    when (modeProvider()) {
                        is ChatMode.Messages -> 1f
                        is ChatMode.ImageDetail -> 1f
                        else -> 0f
                    }
                )
                Text(
                    text = subTitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = TextDecoration.None
                    ),
                    color = contentColor,
                    maxLines = 1,
                    fontSize = subtextFontSize.sp,
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = subTitleAlpha
                            translationY = 12f
                        }
                        .padding(
                            horizontal = LocalSpacing.current.medium,
                            vertical = LocalSpacing.current.extraSmall
                        )
                        .constrainAs(subTitleRef) {
                            centerHorizontallyTo(parent)
                            bottom.linkTo(parent.bottom)
                        }
                )

                val introduceHeight by animateDpAsState(
                    when (modeProvider()) {
                        is ChatMode.ChannelDetail -> 120.dp
                        else -> 0.dp
                    },
                    animationSpec = tween(duration, easing = LinearEasing)
                )
                Text(
                    text = introduce,
                    maxLines = 3,
                    modifier = Modifier
                        .constrainAs(introduceRef) {
                            top.linkTo(iconRef.bottom)
                            bottom.linkTo(parent.bottom)
                            centerHorizontallyTo(parent)
                        }
                        .fillMaxWidth()
                        .height(introduceHeight)
                )
            }
        }
    }
}
