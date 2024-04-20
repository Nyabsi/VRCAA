package cc.sovellus.vrcaa.ui.screen.avatar

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.Avatar
import cc.sovellus.vrcaa.ui.components.misc.BadgesFromTags
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import cc.sovellus.vrcaa.ui.models.avatar.AvatarModel.AvatarState
import cc.sovellus.vrcaa.ui.components.card.AvatarCard
import cc.sovellus.vrcaa.ui.models.avatar.AvatarModel
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import java.text.SimpleDateFormat
import java.util.Locale

class AvatarScreen(
    private val avatarId: String
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val model = rememberScreenModel { AvatarModel(context, avatarId) }

        val state by model.state.collectAsState()

        when (val result = state) {
            is AvatarState.Loading -> LoadingIndicatorScreen().Content()
            is AvatarState.Result -> DisplayResult(result.avatar, model)
            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun DisplayResult(avatar: Avatar?, model: AvatarModel) {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        var isMenuExpanded by remember { mutableStateOf(false) }
        var exitOnce by remember { mutableStateOf(false) }

        if (avatar == null) {
            if (!exitOnce) {
                Toast.makeText(
                    context,
                    stringResource(R.string.avatar_toast_avatar_not_found_message),
                    Toast.LENGTH_SHORT
                ).show()
                navigator.pop()
                exitOnce = true
            }
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.preview_image_description)
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { isMenuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = stringResource(R.string.preview_image_description)
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
                                                if (model.selectAvatar()) {
                                                    Toast.makeText(
                                                        context,
                                                        context.getString(R.string.avatar_dropdown_toast_select_avatar),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            },
                                            text = { Text(stringResource(R.string.avatar_dropdown_label_select)) }
                                        )
                                        // TODO: when implementing favorites, check here if it's favorite or not.
                                        DropdownMenuItem(
                                            onClick = {
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.avatar_dropdown_toast_not_implemented),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            text = { Text(stringResource(R.string.avatar_dropdown_label_favorite)) }
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = padding.calculateTopPadding(),
                                bottom = padding.calculateBottomPadding()
                            ),
                    ) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                avatar.let {
                                    AvatarCard(
                                        thumbnailUrl = it.imageUrl,
                                        name = it.name,
                                        authorName = it.authorName
                                    )
                                }
                            }
                        }

                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.Start
                            ) {
                                SubHeader(title = stringResource(R.string.avatar_title_description))
                                Description(text = avatar.description)

                                val parser =
                                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                                val formatter =
                                    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)

                                val createdAtFormatted = parser.parse(avatar.createdAt)
                                    ?.let { formatter.format(it) }

                                val updatedAtFormatted = parser.parse(avatar.updatedAt)
                                    ?.let { formatter.format(it) }

                                SubHeader(title = stringResource(R.string.avatar_title_created_at))
                                Description(text = createdAtFormatted)

                                SubHeader(title = stringResource(R.string.avatar_title_updated_at))
                                Description(text = updatedAtFormatted)

                                SubHeader(title = stringResource(R.string.avatar_title_content_labels))
                                BadgesFromTags(
                                    tags = avatar.tags,
                                    tagPropertyName = "content",
                                    localizationResourceInt = R.string.avatar_text_content_labels_not_found
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}