package cc.sovellus.vrcaa.ui.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cc.sovellus.vrcaa.ui.screen.friends.FriendsScreen

object FriendsTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Person)

            return remember {
                TabOptions(
                    index = 1u,
                    title = "Friends",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(FriendsScreen())
    }
}