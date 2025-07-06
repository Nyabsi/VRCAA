package cc.sovellus.vrcaa.base

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

abstract class BaseTab : Tab {

    override val options: TabOptions
        @Composable get() = provideOptions()

    @Composable
    protected abstract fun provideOptions(): TabOptions
}