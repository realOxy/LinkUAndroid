package com.linku.im.extension

import androidx.compose.foundation.clickable
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
fun DrawerState.toggle(coroutineScope: CoroutineScope) {
    coroutineScope.launch(Dispatchers.Main) {
        if (isOpen) close()
        else open()
    }
}

operator fun Color.times(alpha: Float): Color {
    return this.copy(alpha = alpha)
}

fun Modifier.intervalClickable(
    enabled: Boolean = true,
    interval: Long = 800,
    onClick: () -> Unit
): Modifier = composed {
    var rememberedTime = remember { 0L }
    Modifier.clickable(enabled) {
        if (System.currentTimeMillis() - rememberedTime >= interval) {
            rememberedTime = System.currentTimeMillis()
            onClick()
        }
    }
}