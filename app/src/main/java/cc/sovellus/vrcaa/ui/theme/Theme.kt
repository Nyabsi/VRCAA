package cc.sovellus.vrcaa.ui.theme


import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

fun isSystemDarkMode(): Boolean {
    val configuration = Resources.getSystem().configuration
    return configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}

@Composable
fun Theme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    MaterialTheme(
        colorScheme = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (isSystemDarkMode())
                    dynamicDarkColorScheme(context)
                else
                    dynamicLightColorScheme(context)
            }

            else -> darkColorScheme()
        },
        typography = Typography(),
        content = content,
    )
}