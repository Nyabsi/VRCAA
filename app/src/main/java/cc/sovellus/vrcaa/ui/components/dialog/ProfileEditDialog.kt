package cc.sovellus.vrcaa.ui.components.dialog

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.User
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.ui.components.input.ComboInput
import kotlinx.coroutines.launch

@Composable
fun ProfileEditDialog(
    onDismiss: () -> Unit,
    onConfirmation: () -> Unit,
    title: String
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    val status = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val bio = remember { mutableStateOf("") }
    val id = remember { mutableStateOf("") }
    val bioLinks = remember { mutableStateListOf("", "", "") }
    val options = listOf("join me", "active", "ask me", "busy")
    val optionFormat = mapOf("join me" to "Join Me", "active" to "Active", "ask me" to "Ask Me", "busy" to "Busy")

    val ageVerified = remember { mutableStateOf(false) }
    val verifiedStatus = remember { mutableStateOf("") }
    val verifiedOptions = listOf("hidden", "verified", "18+")
    val verifiedOptionsFormat = mapOf("hidden" to "Hidden", "verified" to "Verified", "18+" to "18+ Verified")

    LaunchedEffect(Unit) {
        user = CacheManager.getProfile()

        user?.let {
            id.value = it.id
            status.value = it.status
            description.value = it.statusDescription
            bio.value = it.bio

            for (i in 1..it.bioLinks.size)
            {
                bioLinks[i - 1] = it.bioLinks[i - 1]
            }

            ageVerified.value = it.ageVerified
            verifiedStatus.value = it.ageVerificationStatus
        }
    }

    AlertDialog(
        modifier = Modifier.padding(16.dp),

        title = {
            Text(text = title)
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(8.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.profile_edit_dialog_title_status),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Left,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 4.dp, top = 4.dp)
                    )
                }
                item {
                    ComboInput(options = options, selection = status, readableOptions = optionFormat)
                }
                item {
                    Text(
                        text = stringResource(R.string.profile_edit_dialog_title_status_description),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Left,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 4.dp, top = 4.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = description.value,
                        onValueChange = { description.value = it },
                        singleLine = true,
                    )
                }
                if (ageVerified.value) {
                    item {
                        Text(
                            text = stringResource(R.string.profile_edit_dialog_title_age_verification_visibility),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Left,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(bottom = 4.dp, top = 4.dp)
                        )
                    }
                    item {
                        ComboInput(options = verifiedOptions, selection = verifiedStatus, readableOptions = verifiedOptionsFormat)

                    }
                }
                item {
                    Text(
                        text = stringResource(R.string.profile_edit_dialog_title_bio),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Left,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 4.dp, top = 4.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = bio.value,
                        onValueChange = { bio.value = it },
                        minLines = 8
                    )
                }
                item {
                    Text(
                        text = stringResource(R.string.profile_edit_dialog_title_bio_links),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Left,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 4.dp, top = 4.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = bioLinks[0],
                        onValueChange = { bioLinks[0] = it },
                        singleLine = true,
                        modifier = Modifier.padding(bottom = 4.dp, top = 4.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = bioLinks[1],
                        onValueChange = { bioLinks[1] = it },
                        singleLine = true,
                        modifier = Modifier.padding(bottom = 4.dp, top = 4.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = bioLinks[2],
                        onValueChange = { bioLinks[2] = it },
                        singleLine = true,
                        modifier = Modifier.padding(bottom = 4.dp, top = 4.dp)
                    )
                }
            }
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    Toast.makeText(
                        context,
                        context.getString(R.string.profile_edit_dialog_toast_updated),
                        Toast.LENGTH_LONG
                    ).show()
                    coroutineScope.launch {
                        api.user.updateProfileByUserId(id.value, status.value, description.value, bio.value, bioLinks)?.let { user ->
                            CacheManager.updateProfile(user)
                        }
                        onConfirmation()
                    }
                }
            ) {
                Text(stringResource(R.string.profile_edit_dialog_button_update))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    Toast.makeText(
                        context,
                        context.getString(R.string.profile_edit_dialog_toast_cancelled),
                        Toast.LENGTH_LONG
                    ).show()
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.profile_edit_dialog_button_cancel))
            }
        }
    )
}