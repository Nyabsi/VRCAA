package cc.sovellus.vrcaa.ui.components.base

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text

fun LazyListScope.headerAndDescription(
    title: String,
    description: String,
) {
    item {
        ListItem(
            headlineContent = {
                Text(title)
            },
            supportingContent = {
                Text(description)
            }
        )
    }
}