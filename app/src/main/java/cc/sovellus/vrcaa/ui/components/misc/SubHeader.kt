package cc.sovellus.vrcaa.ui.components.misc

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SubHeader(title: String) {
    Text(
        modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp),
        text = title,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Left,
        overflow = TextOverflow.Ellipsis
    )
}