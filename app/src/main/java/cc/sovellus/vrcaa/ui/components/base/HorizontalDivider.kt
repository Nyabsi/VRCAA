package cc.sovellus.vrcaa.ui.components.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

fun LazyListScope.horizontalDivider() {
    item {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .zIndex(1f),
            color = MaterialTheme.colorScheme.outline,
            thickness = 1.dp
        )
    }
}
