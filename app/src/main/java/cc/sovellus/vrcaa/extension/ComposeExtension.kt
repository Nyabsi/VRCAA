package cc.sovellus.vrcaa.extension

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun Modifier.clickableIf(
    enabled: Boolean,
    onClick: () -> Unit
): Modifier = if (enabled) {
    this.clickable(
        onClick = onClick
    )
} else {
    this
}