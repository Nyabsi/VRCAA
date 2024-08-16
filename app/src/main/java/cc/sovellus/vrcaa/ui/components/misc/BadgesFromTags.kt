package cc.sovellus.vrcaa.ui.components.misc

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun BadgesFromTags(
    tags: List<String>,
    tagPropertyName: String,
    localizationResourceInt: Int
) {
    Row(
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 4.dp)
    ) {
        tags.let {
            var found = false
            for (tag in tags) {
                if (tag.contains(tagPropertyName)) {
                    found = true
                    Badge(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .height(height = 26.dp)
                            .padding(2.dp),
                        content = {
                            Text(
                                text = tag.substring(tagPropertyName.length + 1)
                                    .uppercase()
                                    .replace("_", " "),
                                textAlign = TextAlign.Left,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )
                }
            }
            if (!found) {
                Text(
                    text = stringResource(localizationResourceInt),
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}