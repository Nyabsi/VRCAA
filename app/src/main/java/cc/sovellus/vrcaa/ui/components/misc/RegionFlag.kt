package cc.sovellus.vrcaa.ui.components.misc

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R

@Composable
fun RegionFlag(regionId: String) {
    if (regionId.isNotEmpty()) {
        when (regionId.lowercase()) {
            "eu" -> Image(
                painter = painterResource(R.drawable.flag_eu),
                modifier = Modifier.padding(start = 2.dp),
                contentDescription = null
            )

            "jp" -> Image(
                painter = painterResource(R.drawable.flag_jp),
                modifier = Modifier.padding(start = 2.dp),
                contentDescription = null
            )

            "us" -> Image(
                painter = painterResource(R.drawable.flag_us),
                modifier = Modifier.padding(start = 2.dp),
                contentDescription = null
            )

            "use" -> Image(
                painter = painterResource(R.drawable.flag_us),
                modifier = Modifier.padding(start = 2.dp),
                contentDescription =null
            )

            "usw" -> Image(
                painter = painterResource(R.drawable.flag_us),
                modifier = Modifier.padding(start = 2.dp),
                contentDescription = null
            )
        }
    } else {
        Image(
            painter = painterResource(R.drawable.flag_us),
            modifier = Modifier.padding(start = 2.dp),
            contentDescription = null
        )
    }
}