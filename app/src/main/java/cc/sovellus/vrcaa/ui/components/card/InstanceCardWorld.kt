package cc.sovellus.vrcaa.ui.components.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.Instance
import cc.sovellus.vrcaa.helper.LocationHelper

@Composable
fun InstanceCardWorld(intent: String, instance: Instance, onClick: () -> Unit) {
    val result = LocationHelper.parseLocationInfo(intent)
    ListItem(
        headlineContent = {
            Text("Capacity: ${instance.nUsers}/${instance.world.capacity}, ${result.instanceType}")
        },
        overlineContent = {
            Text("${instance.world.name} #${instance.name}")
        },
        trailingContent = {
            if (result.regionId.isNotEmpty()) {
                when (result.regionId.lowercase()) {
                    "eu" -> Image(
                        painter = painterResource(R.drawable.flag_eu),
                        modifier = Modifier.padding(start = 2.dp),
                        contentDescription = "Region flag"
                    )
                    "jp" -> Image(
                        painter = painterResource(R.drawable.flag_jp),
                        modifier = Modifier.padding(start = 2.dp),
                        contentDescription = "Region flag"
                    )
                    "us" -> Image(
                        painter = painterResource(R.drawable.flag_us),
                        modifier = Modifier.padding(start = 2.dp),
                        contentDescription = "Region flag"
                    )
                    "use" -> Image(
                        painter = painterResource(R.drawable.flag_us),
                        modifier = Modifier.padding(start = 2.dp),
                        contentDescription = "Region flag"
                    )
                    "usw" -> Image(
                        painter = painterResource(R.drawable.flag_us),
                        modifier = Modifier.padding(start = 2.dp),
                        contentDescription = "Region flag"
                    )
                }
            } else {
                Image(
                    painter = painterResource(R.drawable.flag_us),
                    modifier = Modifier.padding(start = 2.dp),
                    contentDescription = "Region flag",
                )
            }
        },
        modifier = Modifier.clickable(
            onClick = {
                onClick()
            }
        )
    )
}