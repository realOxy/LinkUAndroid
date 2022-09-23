package com.linku.im.screen.chat.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.linku.im.extension.friendlyFormatted
import com.linku.im.ui.theme.LocalSpacing
import com.linku.im.ui.theme.LocalTheme

@Composable
fun ChatTimestamp(
    timestamp: Long,
    modifier: Modifier = Modifier
) {
    val friendlyFormatted = remember(timestamp) { timestamp.friendlyFormatted }
    Text(
        text = friendlyFormatted,
        modifier = modifier
            .clip(RoundedCornerShape(30))
            .background(LocalTheme.current.background)
            .padding(
                vertical = LocalSpacing.current.extraSmall,
                horizontal = LocalSpacing.current.small
            ),
        style = MaterialTheme.typography.bodySmall
            .copy(
                fontWeight = FontWeight.Bold
            ),
        color = LocalTheme.current.onSurface
    )
}