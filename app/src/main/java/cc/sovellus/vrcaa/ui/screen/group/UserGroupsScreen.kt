package cc.sovellus.vrcaa.ui.screen.group

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.UserGroups
import cc.sovellus.vrcaa.ui.components.card.GroupCard
import cc.sovellus.vrcaa.ui.models.group.UserGroupsModel
import cc.sovellus.vrcaa.ui.models.group.UserGroupsModel.UserGroupsState
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen

class UserGroupsScreen(
    private val username: String,
    private val userId: String
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val context = LocalContext.current

        val model = rememberScreenModel { UserGroupsModel(context, userId) }

        val state by model.state.collectAsState()

        when (val result = state) {
            is UserGroupsState.Loading -> LoadingIndicatorScreen().Content()
            is UserGroupsState.Result -> ShowGroups(result.groups)

            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowGroups(
        groups: UserGroups?
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        var isMenuExpanded by remember { mutableStateOf(false) }

        if (groups == null) {
            Toast.makeText(
                context,
                stringResource(R.string.group_user_failed_to_fetch_groups),
                Toast.LENGTH_SHORT
            ).show()
        } else {
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
                        title = { Text(text = stringResource(R.string.group_user_viewing_groups_username).format(username)) }
                    )
                },
                content = { padding ->
                    Column(
                        modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (groups.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = stringResource(R.string.group_user_no_groups_message))
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(groups.size) {
                                    val group = groups[it]
                                    GroupCard(
                                        groupName = group.name,
                                        shortCode = group.shortCode,
                                        discriminator = group.discriminator,
                                        bannerUrl = group.bannerUrl,
                                        iconUrl = group.iconUrl,
                                        totalMembers = group.memberCount
                                    ) {
                                        navigator.push(GroupScreen(group.groupId))
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}