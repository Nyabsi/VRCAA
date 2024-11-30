package cc.sovellus.vrcaa.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext

@Composable
fun Theme(theme: Int, content: @Composable () -> Unit) {
    val context = LocalContext.current

    MaterialTheme(
        colorScheme = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                when (theme) {
                    0 -> dynamicLightColorScheme(context)
                    1 -> dynamicDarkColorScheme(context)
                    else -> {
                        if (isSystemInDarkTheme())
                            dynamicDarkColorScheme(context)
                        else
                            dynamicLightColorScheme(context)
                    }
                }
            }
            else -> {
                when (theme) {
                    0 -> lightColorScheme()
                    1 -> darkColorScheme()
                    else -> {
                        if (isSystemInDarkTheme())
                            darkColorScheme()
                        else
                            lightColorScheme()
                    }
                }
            }
        },
        typography = Typography(),
        content = content,
    )
}

val LocalTheme = compositionLocalOf { 2 }