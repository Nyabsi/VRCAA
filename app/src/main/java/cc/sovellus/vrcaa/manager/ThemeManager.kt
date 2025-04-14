package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.base.BaseManager

object ThemeManager : BaseManager<ThemeManager.ThemeListener>() {

    interface ThemeListener {
        fun onPreferenceUpdate(theme: Int)
    }

    fun setTheme(theme: Int) {
        getListeners().forEach { listener ->
            listener.onPreferenceUpdate(theme)
        }
    }
}