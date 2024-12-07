package cc.sovellus.vrcaa.ui.components.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

fun LazyListScope.button(
    text: Int,
    description: Int,
    trailingPageIndicator: Boolean = false,
    trailingPageIndicatorGoesOutsideApp: Boolean = false,
    onClick: () -> Unit
) {
    item {
        ListItem(
            headlineContent = { if (text != 0) Text(stringResource(text)) },
            supportingContent = { if (description != 0) Text(stringResource(description)) },
            trailingContent = {
                if (trailingPageIndicator) if (trailingPageIndicatorGoesOutsideApp) Icon(
                    Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                ) else Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
            },
            modifier = Modifier.clickable {
                onClick()
            }
        )
    }
}