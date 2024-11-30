package cc.sovellus.vrcaa.manager

object ThemeManager {
    private var themeListeners: MutableList<ThemeListener> = mutableListOf()

    interface ThemeListener {
        fun onPreferenceUpdate(theme: Int)
    }

    fun addThemeListener(listener: ThemeListener) {
        themeListeners.add(listener)
    }

    fun setTheme(theme: Int) {
        themeListeners.map {
            it.onPreferenceUpdate(theme)
        }
    }
}