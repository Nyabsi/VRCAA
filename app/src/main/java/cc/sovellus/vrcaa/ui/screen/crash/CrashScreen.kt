package cc.sovellus.vrcaa.ui.screen.crash

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import cc.sovellus.vrcaa.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrashScreen(
    exception: String,
    onRestart: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.crash_title_text)) }
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    ),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                item {
                    Text(
                        text = exception,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = padding.calculateBottomPadding()),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { onRestart() }) {
                    Text(text = stringResource(R.string.crash_button_restart_text))
                }
                Button(onClick = {
                    val clipboard: ClipboardManager? = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                    val clip = ClipData.newPlainText("Crash Log", exception)
                    clipboard?.setPrimaryClip(clip)
                }) {
                    Text(text = stringResource(R.string.crash_button_copy_log_text))
                }
                Button(onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://discord.gg/aJs8qJXuT3")
                    )
                    context.startActivity(intent)
                }) {
                    Text(text = stringResource(R.string.crash_button_report_text))
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
