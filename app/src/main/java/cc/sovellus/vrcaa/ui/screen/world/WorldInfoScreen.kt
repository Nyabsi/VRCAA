package cc.sovellus.vrcaa.ui.screen.world


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.http.models.Instance
import cc.sovellus.vrcaa.api.http.models.World
import cc.sovellus.vrcaa.ui.components.card.InstanceCardWorld
import cc.sovellus.vrcaa.ui.components.misc.BadgesFromTags
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldInfoScreenModel.WorldInfoState
import cc.sovellus.vrcaa.ui.components.card.WorldCard
import java.text.SimpleDateFormat
import java.util.Locale

class WorldInfoScreen(
    private val worldId: String
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val context = LocalContext.current

        val model = rememberScreenModel { WorldInfoScreenModel(context, worldId) }
        val state by model.state.collectAsState()

        when (val result = state) {
            is WorldInfoState.Loading -> LoadingIndicatorScreen().Content()
            is WorldInfoState.Result -> MultiChoiceHandler(result.world, result.instances, model)
            else -> {}
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MultiChoiceHandler(
        world: World?,
        instances: MutableList<Pair<String, Instance>>,
        model: WorldInfoScreenModel
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        if (world == null) {
            Toast.makeText(
                context,
                "World is private, or it doesn't exist.",
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

                        title = { Text(text = world.name) }
                    )
                },
                content = {

                    val options =  arrayOf("Info", "Instances") // stringArrayResource(R.array.search_selection_options)
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
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            world.let {
                WorldCard(
                    url = it.imageUrl,
                    name = it.name,
                    author = "By ${it.authorName}"
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            SubHeader(title = stringResource(R.string.world_label_description))
            Description(text = world.description)

            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)

            val createdAtFormatted = parser.parse(world.createdAt)
                ?.let { formatter.format(it) }

            val updatedAtFormatted = parser.parse(world.updatedAt)
                ?.let { formatter.format(it) }

            SubHeader(title = stringResource(R.string.world_title_created_at))
            Description(text = createdAtFormatted)

            SubHeader(title = stringResource(R.string.world_title_updated_at))
            Description(text = updatedAtFormatted)

            SubHeader(title = stringResource(R.string.world_label_tags))
            BadgesFromTags(
                tags = world.tags,
                tagPropertyName = "author_tag",
                localizationResourceInt = R.string.world_text_no_tags
            )
        }
    }

    @Composable
    fun ShowInstances(instances: MutableList<Pair<String, Instance>>, model: WorldInfoScreenModel) {
        val dialogState = remember { mutableStateOf(false) }

        if (dialogState.value) {
            InviteDialog(
                onDismiss = { dialogState.value = false },
                onConfirmation = {
                    dialogState.value = false
                    model.selfInvite()
                },
                title = "Are you sure?",
                description = "You're about to send invite to yourself onto this instance, do you really want to do that?"
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (instances.isEmpty()) {
                item {
                    Text(text = "No public instances")
                }
            } else {
                items(instances.size) {
                    val instance = instances[it]
                    InstanceCardWorld(
                        intent = instance.first,
                        instance = instance.second,
                        onClick = {
                            dialogState.value = true
                            model.clickedInstance.value = instance.second.id
                        }
                    )
                }
            }
        }
    }
}