package cc.sovellus.vrcaa.ui.screen.about

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel

class AboutScreenModel(
    developerMode: Boolean
) : ScreenModel {
    val crashAnalytics = mutableStateOf(developerMode)
}