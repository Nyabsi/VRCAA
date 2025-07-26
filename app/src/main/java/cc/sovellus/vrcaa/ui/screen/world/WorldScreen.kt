/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.ui.screen.world

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
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
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.ui.components.card.WorldCard
import cc.sovellus.vrcaa.ui.components.dialog.FavoriteDialog
import cc.sovellus.vrcaa.ui.components.dialog.GenericDialog
import cc.sovellus.vrcaa.ui.components.layout.InstanceItem
import cc.sovellus.vrcaa.ui.components.misc.BadgesFromTags
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import java.text.NumberFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

class WorldScreen(
    private val worldId: String,
    private val peek: Boolean = false
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val model = rememberScreenModel { WorldScreenModel(worldId) }
        val state by model.state.collectAsState()

        when (val result = state) {
            is WorldScreenModel.WorldInfoState.Loading -> LoadingIndicatorScreen().Content()
            is WorldScreenModel.WorldInfoState.Failure -> HandleFailure()
            is WorldScreenModel.WorldInfoState.Result -> MultiChoiceHandler(model, result.world, result.instances)
            else -> {}
        }
    }

    @Composable
    private fun HandleFailure() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        Toast.makeText(
            context,
            stringResource(R.string.world_toast_not_found),
            Toast.LENGTH_SHORT
        ).show()

        if (peek) {
            if (context is Activity) {
                context.finish()
            }
        } else {
            navigator.pop()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MultiChoiceHandler(
        model: WorldScreenModel,
        world: World?,
        instances: List<Pair<String, Instance?>>,
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        var isMenuExpanded by remember { mutableStateOf(false) }
        var favoriteDialogShown by remember { mutableStateOf(false) }

        if (world == null) {
            Toast.makeText(
                context,
                stringResource(R.string.world_toast_not_found),
                Toast.LENGTH_SHORT
            ).show()
            navigator.pop()
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = {
                                if (peek) {
                                    if (context is Activity) {
                                        context.finish()
                                    }
                                } else {
                                    navigator.pop()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Go Back"
                                )
                            }
                        },

                        title = {
                            Text(
                                text = world.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
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
                                                navigator.push(
                                                    UserProfileScreen(world.authorId)
                                                )
                                                isMenuExpanded = false
                                            },
                                            text = { Text(stringResource(R.string.group_page_dropdown_view_author)) }
                                        )
                                        if (FavoriteManager.isFavorite("world", world.id)) {
                                            DropdownMenuItem(
                                                onClick = {
                                                    model.removeFavorite(world)
                                                    isMenuExpanded = false
                                                },
                                                text = { Text(stringResource(R.string.favorite_label_remove)) }
                                            )
                                        } else {
                                            DropdownMenuItem(
                                                onClick = {
                                                    favoriteDialogShown = true
                                                    isMenuExpanded = false
                                                },
                                                text = { Text(stringResource(R.string.favorite_label_add)) }
                                            )
                                        }
                                        DropdownMenuItem(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText(null, world.id)
                                                clipboard.setPrimaryClip(clip)

                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.copied_toast).format(world.name),
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                isMenuExpanded = false
                                            },
                                            text = { Text(stringResource(R.string.copy_id_label)) }
                                        )
                                    }
                                }
                            }
                        }
                    )
                },
                content = {

                    if (favoriteDialogShown) {
                        FavoriteDialog(
                            type = IFavorites.FavoriteType.FAVORITE_WORLD,
                            id = world.id,
                            metadata = FavoriteManager.FavoriteMetadata(
                                world.id,
                                "",
                                world.name,
                                world.thumbnailImageUrl
                            ),
                            onDismiss = { favoriteDialogShown = false },
                            onConfirmation = { favoriteDialogShown = false }
                        )
                    }

                    val options = stringArrayResource(R.array.world_selection_options)
                    val icons = listOf(Icons.Filled.Cabin, Icons.Filled.LocationOn)

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(
                                top = it.calculateTopPadding(),
                                bottom = it.calculateBottomPadding()
                            ),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MultiChoiceSegmentedButtonRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                        ) {
                            options.forEachIndexed { index, label ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = options.size
                                    ),
                                    icon = {
                                        SegmentedButtonDefaults.Icon(
                                            active = index == model.currentTabIndex.intValue,
                                            activeContent = {
                                                Icon(
                                                    imageVector = Icons.Filled.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(SegmentedButtonDefaults.IconSize).offset(y = 2.5.dp)
                                                )
                                            },
                                            inactiveContent = {
                                                Icon(
                                                    imageVector = icons[index],
                                                    contentDescription = null,
                                                    modifier = Modifier.size(SegmentedButtonDefaults.IconSize).offset(y = 2.5.dp)
                                                )
                                            }
                                        )
                                    },
                                    onCheckedChange = {
                                        model.currentTabIndex.intValue = index
                                    },
                                    checked = index == model.currentTabIndex.intValue
                                ) {
                                    Text(text = label, softWrap = true, maxLines = 1)
                                }
                            }
                        }

                        when (model.currentTabIndex.intValue) {
                            0 -> ShowInfo(world)
                            1 -> ShowInstances(instances, model)
                        }
                    }
                }
            )
        }
    }

    @Composable
    fun ShowInfo(world: World) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                WorldCard(world)

                Column(
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start,
                ) {
                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .widthIn(Dp.Unspecified, 520.dp)
                    ) {
                        SubHeader(title = stringResource(R.string.world_label_description))
                        Description(text = world.description)
                    }

                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .widthIn(Dp.Unspecified, 520.dp)
                    ) {
                        val userTimeZone = TimeZone.getDefault().toZoneId()
                        val formatter = DateTimeFormatter.ofLocalizedDateTime(java.time.format.FormatStyle.SHORT)
                            .withLocale(Locale.getDefault())

                        val createdAtFormatted = ZonedDateTime.parse(world.createdAt).withZoneSameInstant(userTimeZone).format(formatter)
                        val updatedAtFormatted = ZonedDateTime.parse(world.updatedAt).withZoneSameInstant(userTimeZone).format(formatter)

                        SubHeader(title = stringResource(R.string.world_title_occupants))
                        Description(text = "Public (${NumberFormat.getInstance().format(world.publicOccupants)}) Private (${NumberFormat.getInstance().format(world.privateOccupants)}) Total (${NumberFormat.getInstance().format(world.occupants)})")

                        val occupancyRate = world.visits.takeIf { it != 0 }?.let {
                            String.format(Locale.ENGLISH, "%.1f", world.favorites.toFloat() / it.toFloat() * 100)
                        } ?: "0.0%"

                        SubHeader(title = stringResource(R.string.world_title_favorites))
                        Description(text = "${NumberFormat.getInstance().format(world.favorites)} (${occupancyRate}%)")

                        SubHeader(title = stringResource(R.string.world_title_visits))
                        Description(text = NumberFormat.getInstance().format(world.visits))

                        SubHeader(title = stringResource(R.string.world_title_capacity))
                        Description(text = "${world.recommendedCapacity} (${world.capacity})")

                        SubHeader(title = stringResource(R.string.world_title_created_at))
                        Description(text = createdAtFormatted)

                        SubHeader(title = stringResource(R.string.world_title_updated_at))
                        Description(text = updatedAtFormatted)

                        SubHeader(title = stringResource(R.string.world_title_publication_date))
                        Description(text = updatedAtFormatted)

                        SubHeader(title = "${stringResource(R.string.world_title_heat)} (${world.heat})")

                        Row(
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            for (i in 0..world.heat) {
                                Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(28.dp).padding(4.dp))
                            }
                        }

                        SubHeader(title = "${stringResource(R.string.world_title_popularity)} (${world.popularity})")

                        Row(
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            for (i in 0..world.popularity) {
                                Icon(Icons.Filled.Favorite, contentDescription = null, modifier = Modifier.size(28.dp).padding(4.dp))
                            }
                        }

                        SubHeader(title = stringResource(R.string.world_label_tags))
                        BadgesFromTags(
                            tags = world.tags,
                            tagPropertyName = "author_tag",
                            localizationResourceInt = R.string.world_text_no_tags
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ShowInstances(
        instances: List<Pair<String, Instance?>>,
        model: WorldScreenModel
    ) {
        val dialogState = remember { mutableStateOf(false) }

        if (dialogState.value) {
            GenericDialog(
                onDismiss = { dialogState.value = false },
                onConfirmation = {
                    dialogState.value = false
                    model.selfInvite()
                },
                title = stringResource(R.string.world_instance_invite_dialog_title),
                description = stringResource(R.string.world_instance_invite_dialog_description)
            )
        }

        if (instances.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(stringResource(R.string.world_instance_no_public_instances_message))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(instances) { instance ->
                    instance.second?.let { instanceObj ->
                        InstanceItem(
                            intent = instance.first,
                            instance = instanceObj,
                            onClick = {
                                dialogState.value = true
                                model.selectedInstanceId.value = instanceObj.id
                            }
                        )
                    }
                }
            }
        }
    }
}
