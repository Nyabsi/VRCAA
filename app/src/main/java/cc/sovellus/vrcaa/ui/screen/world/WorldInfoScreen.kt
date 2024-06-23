package cc.sovellus.vrcaa.ui.screen.world


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.Instance
import cc.sovellus.vrcaa.api.vrchat.models.World
import cc.sovellus.vrcaa.ui.components.misc.BadgesFromTags
import cc.sovellus.vrcaa.ui.components.misc.Description
import cc.sovellus.vrcaa.ui.components.misc.SubHeader
import cc.sovellus.vrcaa.ui.screen.misc.LoadingIndicatorScreen
import cc.sovellus.vrcaa.ui.models.world.WorldInfoModel.WorldInfoState
import cc.sovellus.vrcaa.ui.components.card.WorldCard
import cc.sovellus.vrcaa.ui.components.dialog.GenericDialog
import cc.sovellus.vrcaa.ui.components.layout.InstanceItem
import cc.sovellus.vrcaa.ui.models.world.WorldInfoModel
import java.text.SimpleDateFormat
import java.util.Locale

class WorldInfoScreen(
    private val worldId: String
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val context = LocalContext.current

        val model = rememberScreenModel { WorldInfoModel(context, worldId) }
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
        model: WorldInfoModel
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

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
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Go Back"
                                )
                            }
                        },

                        title = { Text(
                            text = world.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        ) }
                    )
                },
                content = {

                    val options =  stringArrayResource(R.array.world_selection_options)
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
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                world.let {
                    WorldCard(
                        url = it.imageUrl,
                        name = it.name,
                        author = stringResource(R.string.world_author_label).format(it.authorName)
                    )
                }
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start,
                ) {
                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
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
            }
        }
    }

    @Composable
    fun ShowInstances(instances: MutableList<Pair<String, Instance>>, model: WorldInfoModel) {
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
            if (instances.isEmpty()) {
                item {
                    Text(stringResource(R.string.world_instance_no_public_instances_message))
                }
            } else {
                items(instances.size) {
                    val instance = instances[it]
                    InstanceItem(
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