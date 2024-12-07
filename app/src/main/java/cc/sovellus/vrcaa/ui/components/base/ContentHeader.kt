package cc.sovellus.vrcaa.ui.components.base

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

fun LazyListScope.ContentHeader(
    text: Int,
    modifier: Modifier = Modifier.padding(4.dp)
) {
    item {
        Spacer(modifier = modifier)
        ListItem(
            headlineContent = {
                Text(
                    stringResource(text),
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )
            },
        )
    }
}