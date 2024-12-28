package cc.sovellus.vrcaa.ui.screen.avatar

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites
import cc.sovellus.vrcaa.api.vrchat.http.models.Avatar
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.ui.components.card.AvatarCard
import cc.sovellus.vrcaa.ui.components.dialog.FavoriteDialog
import cc.sovellus.vrcaa.ui.components.misc.BadgesFromTags
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

class AvatarScreen(
    private val avatarId: String
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val model = rememberScreenModel { AvatarScreenModel(avatarId) }

        val state by model.state.collectAsState()

        when (val result = state) {
            is AvatarScreenModel.AvatarState.Loading -> LoadingIndicatorScreen().Content()
            is AvatarScreenModel.AvatarState.Failure -> HandleFailure()
            is AvatarScreenModel.AvatarState.Result -> HandleResult(result.avatar, model)
            else -> {}
        }
    }

    @Composable
    private fun HandleFailure() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        Toast.makeText(
            context,
            stringResource(R.string.avatar_toast_avatar_not_found_message),
            Toast.LENGTH_SHORT
        ).show()

        navigator.pop()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun HandleResult(avatar: Avatar, model: AvatarScreenModel) {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        var isMenuExpanded by remember { mutableStateOf(false) }
        var isDialogShown by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isMenuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = null
                            )

                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                DropdownMenu(
                                    expanded = isMenuExpanded,
                                    onDismissRequest = { isMenuExpanded = false },
                                    offset = DpOffset(0.dp, 0.dp)
                                ) {
                                    DropdownMenuItem(
                                        onClick = {
                                            model.selectAvatar()
                                        },
                                        text = { Text(stringResource(R.string.avatar_dropdown_label_select)) }
                                    )
                                    if (FavoriteManager.isFavorite("avatar", avatar.id)) {
                                        DropdownMenuItem(
                                            onClick = {
                                                model.removeFavorite()
                                                isMenuExpanded = false
                                            },
                                            text = { Text(stringResource(R.string.favorite_label_remove)) }
                                        )
                                    } else {
                                        DropdownMenuItem(
                                            onClick = {
                                                isDialogShown = true
                                                isMenuExpanded = false
                                            },
                                            text = { Text(stringResource(R.string.favorite_label_add)) }
                                        )
                                    }
                                    DropdownMenuItem(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText(null, avatar.id)
                                            clipboard.setPrimaryClip(clip)

                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.copied_toast).format(avatar.name),
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            isMenuExpanded = false
                                        },
                                        text = { Text(stringResource(R.string.copy_id_label)) }
                                    )
                                }
                            }
                        }
                    },
                    title = {
                        Text(text = avatar.name)
                    }
                )
            },
            content = { padding ->

                if (isDialogShown) {
                    FavoriteDialog(
                        type = IFavorites.FavoriteType.FAVORITE_AVATAR,
                        id = avatar.id,
                        metadata = FavoriteManager.FavoriteMetadata(
                            avatar.id,
                            "",
                            avatar.name,
                            avatar.thumbnailImageUrl
                        ),
                        onDismiss = { isDialogShown = false },
                        onConfirmation = { isDialogShown = false }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = padding.calculateTopPadding(),
                            bottom = padding.calculateBottomPadding(),
                            start = 16.dp,
                            end = 16.dp
                        )
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AvatarCard(avatar)

                    Spacer(modifier = Modifier.padding(8.dp))

                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier.widthIn(Dp.Unspecified, 520.dp)
                    ) {
                        SubHeader(title = stringResource(R.string.avatar_title_description))
                        Description(text = avatar.description)

                        val userTimeZone = TimeZone.getDefault().toZoneId()
                        val formatter = DateTimeFormatter.ofLocalizedDateTime(java.time.format.FormatStyle.SHORT)
                            .withLocale(Locale.getDefault())

                        val createdAtFormatted = ZonedDateTime.parse(avatar.createdAt).withZoneSameInstant(userTimeZone).format(formatter)
                        val updatedAtFormatted = ZonedDateTime.parse(avatar.updatedAt).withZoneSameInstant(userTimeZone).format(formatter)

                        SubHeader(title = stringResource(R.string.avatar_title_created_at))
                        Description(text = createdAtFormatted)

                        SubHeader(title = stringResource(R.string.avatar_title_updated_at))
                        Description(text = updatedAtFormatted)

                        SubHeader(title = stringResource(R.string.avatar_title_version))
                        Description(text = avatar.version.toString())

                        val platforms = avatar.unityPackages.joinToString(", ") {
                            when (it.platform) {
                                "standalonewindows" -> "PC"
                                "android" -> "Android"
                                "ios" -> "iOS"
                                else -> it.platform
                            }
                        }

                        SubHeader(title = stringResource(R.string.avatar_title_platform))
                        Description(text = platforms)

                        SubHeader(title = stringResource(R.string.avatar_title_content_labels))
                        BadgesFromTags(
                            tags = avatar.tags,
                            tagPropertyName = "content",
                            localizationResourceInt = R.string.avatar_text_content_labels_not_found
                        )
                    }
                }
            }
        )
    }
}
