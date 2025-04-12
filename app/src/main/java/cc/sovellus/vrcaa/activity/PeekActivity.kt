package cc.sovellus.vrcaa.activity


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.transitions.SlideTransition
import cc.sovellus.vrcaa.base.BaseActivity
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarScreen
import cc.sovellus.vrcaa.ui.screen.group.GroupScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldInfoScreen

class PeekActivity : BaseActivity() {

    lateinit var type: String
    lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var path = intent.data?.path
        path = path?.substring(6)

        val data = path?.split("/")

        type = data?.get(0) ?: "unknown"
        id = data?.get(1) ?: ""
    }

    @Composable
    override fun Content(bundle: Bundle?) {
        var screen: Screen? = null

        when (type) {
            "world" -> {
                screen = WorldInfoScreen(id, true)
            }
            "user" -> {
                screen = UserProfileScreen(id, true)
            }
            "avatar" -> {
                screen = AvatarScreen(id, true)
            }
            "group" -> {
                screen = GroupScreen(id, true)
            }
            else -> finish()
        }

        screen?.let {
            Navigator(
                screen = it,
                disposeBehavior = NavigatorDisposeBehavior(
                    disposeNestedNavigators = false,
                    disposeSteps = false
                ),
                onBackPressed = { true }
            ) { navigator ->
                SlideTransition(navigator = navigator)
            }
        }
    }
}