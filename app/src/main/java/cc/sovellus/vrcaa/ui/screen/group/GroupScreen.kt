package cc.sovellus.vrcaa.ui.screen.group

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.Group
import cc.sovellus.vrcaa.api.vrchat.models.GroupInstances
import cc.sovellus.vrcaa.ui.components.card.GroupCard
import cc.sovellus.vrcaa.ui.components.card.InstanceCardGroup
import cc.sovellus.vrcaa.ui.components.dialog.GenericDialog
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.misc.Languages
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import cc.sovellus.vrcaa.ui.models.group.GroupModel
import cc.sovellus.vrcaa.ui.models.group.GroupModel.GroupState
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen

class GroupScreen(
    private val groupId: String
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val context = LocalContext.current

        val model = rememberScreenModel { GroupModel(context, groupId) }

        val state by model.state.collectAsState()

        when (val result = state) {
            is GroupState.Loading -> LoadingIndicatorScreen().Content()
            is GroupState.Result -> MultiChoiceHandler(result.group, result.instances, model)
            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MultiChoiceHandler(
        group: Group?,
        instances: GroupInstances?,
        model: GroupModel
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        var isMenuExpanded by remember { mutableStateOf(false) }

        if (group == null) {
            Toast.makeText(
                context,
                stringResource(R.string.group_toast_not_found_message),
                Toast.LENGTH_SHORT
            ).show()
            navigator.pop()
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Go Back"
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
                                                navigator.push(
                                                    UserProfileScreen(group.ownerId)
                                                )
                                                isMenuExpanded = false
                                            },
                                            text = { Text(stringResource(R.string.group_page_dropdown_view_author)) }
                                        )
                                        if (group.membershipStatus == "inactive") {
                                            if (group.joinState != "closed" && group.joinState != "invite") {
                                                DropdownMenuItem(
                                                    onClick = {
                                                        if (group.joinState == "open") {
                                                            model.joinGroup(true)
                                                        } else {
                                                            model.joinGroup(false)
                                                        }
                                                        isMenuExpanded = false
                                                    },
                                                    text = { Text(
                                                        text = if (group.joinState == "open") {
                                                            stringResource(R.string.group_page_dropdown_join_group)
                                                        } else {
                                                            stringResource(R.string.group_page_dropdown_request_invite)
                                                        }
                                                    ) }
                                                )
                                            }
                                        } else {
                                            if (group.membershipStatus == "requested") {
                                                DropdownMenuItem(
                                                    onClick = {
                                                        model.withdrawInvite()
                                                        isMenuExpanded = false
                                                    },
                                                    text = { Text(stringResource(R.string.group_page_dropdown_request_invite_cancel)) }
                                                )
                                            } else {
                                                DropdownMenuItem(
                                                    onClick = {
                                                        model.leaveGroup()
                                                        isMenuExpanded = false
                                                    },
                                                    text = { Text(stringResource(R.string.group_page_dropdown_leave_group)) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        title = { Text(
                            text = group.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        ) }
                    )
                },
                content = {

                    val options =  stringArrayResource(R.array.group_selection_options)
                    val icons = listOf(Icons.Filled.Cabin, Icons.Filled.LocationOn)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
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
                                .padding(start = 16.dp, end = 16.dp)
                        ) {
                            options.forEachIndexed { index, label ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = options.size
                                    ),
                                    icon = {
                                        SegmentedButtonDefaults.Icon(active = index == model.currentIndex.intValue) {
                                            Icon(
                                                imageVector = icons[index],
                                                contentDescription = null,
                                                modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                                            )
                                        }
                                    },
                                    onCheckedChange = {
                                        model.currentIndex.intValue = index
                                    },
                                    checked = index == model.currentIndex.intValue
                                ) {
                                    Text(text = label, softWrap = true, maxLines = 1)
                                }
                            }
                        }

                        when (model.currentIndex.intValue) {
                            0 -> ShowGroupInfo(group)
                            1 -> ShowGroupInstances(model, instances)
                        }
                    }
                }
            )
        }
    }

    @Composable
    private fun ShowGroupInfo(group: Group) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    group.let {
                        GroupCard(
                            groupName = it.name,
                            discriminator = it.discriminator,
                            shortCode = it.shortCode,
                            bannerUrl = it.bannerUrl,
                            iconUrl = it.iconUrl,
                            totalMembers = it.memberCount
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
                    SubHeader(title = stringResource(R.string.group_page_label_description))
                    Description(text = group.description)

                    SubHeader(title = stringResource(R.string.group_page_label_rules))
                    Description(text = group.rules)

                    SubHeader(title = stringResource(R.string.profile_label_languages))
                    Languages(languages = group.languages, true)
                }
            }
        }
    }

    @Composable
    private fun ShowGroupInstances(
        model: GroupModel,
        instances: GroupInstances?
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

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (instances == null) {
                item {
                    Text(stringResource(R.string.world_instance_no_public_instances_message))
                }
            } else {
                if (instances.isEmpty()) {
                    item {
                        Text(stringResource(R.string.world_instance_no_public_instances_message))
                    }
                } else {
                    items(instances.size) {
                        val instance = instances[it]
                        InstanceCardGroup(
                            intent = instance.location,
                            world = instance.world,
                            members = instance.memberCount,
                            instanceId = instance.instanceId
                        ) {
                            dialogState.value = true
                            model.clickedInstance.value = instance.location
                        }
                    }
                }
            }
        }
    }
}