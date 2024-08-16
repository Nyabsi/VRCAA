package cc.sovellus.vrcaa.ui.screen.settings

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel

class SettingsScreenModel(
    developerMode: Boolean
) : ScreenModel {
    val developerMode = mutableStateOf(developerMode)
}