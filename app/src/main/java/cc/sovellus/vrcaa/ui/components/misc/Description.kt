package cc.sovellus.vrcaa.ui.components.misc

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R

@Composable
fun Description(text: String?) {
    Column(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp, bottom = 4.dp).fillMaxWidth()
    ) {
        Text(
            text = if (text.isNullOrEmpty()) {
                stringResource(R.string.profile_text_no_biography)
            } else {
                text
            },
            textAlign = TextAlign.Left,
            fontWeight = FontWeight.SemiBold
        )
    }
}