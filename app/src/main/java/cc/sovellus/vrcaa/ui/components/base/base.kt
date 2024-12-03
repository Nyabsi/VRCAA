package cc.sovellus.vrcaa.ui.components.base

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.DisabledByDefault
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.ui.screen.favorites.FavoritesScreen
import cc.sovellus.vrcaa.ui.screen.feed.FeedScreen
import cc.sovellus.vrcaa.ui.screen.friends.FriendsScreen
import cc.sovellus.vrcaa.ui.screen.home.HomeScreen

/*
How to use:

import the things you need
THEN place the functions inside a scaffold,
like you would put "item {...}".

Making a switch item w/o a description:

SwitchItem(
    R.string.ExampleTitle,
    0,
    model.example
) {
    toggled -> example = toggled
}

Remember, R.string returns an Int.
If the text is not needed, pass 0.

Hopefully the rest of the functions make sense.

If there is an event (OnClick, OnChange, etc.), you can run things like this:
ExampleFunction(Parameter) {OutParameter -> ...}

Otherwise, you can just do this:
ExampleFunction(Parameter)
 */


class ComposableBase {



    companion object {

        fun QuickToast(context: Context, text: Int) {
            Toast.makeText(
                context,
                context.getString(R.string.developer_mode_toggle_toast),
                Toast.LENGTH_SHORT
            ).show()
        }

        fun DefaultIcon():ImageVector {return Icons.Outlined.DisabledByDefault} // Change the default icon here, I can't figure out how to make it nothing.

        fun LazyListScope.SwitchItem(
            text: Int,
            description: Int,
            mutableState_Variable: MutableState<Boolean>,
            OnToggle: (Boolean) -> Unit
        ) {
            item {
                ListItem(
                    headlineContent = {if (text != 0) Text(stringResource(text))},
                    supportingContent = { if (description != 0) Text(stringResource(description))},
                    trailingContent = {
                        Switch(
                            checked = mutableState_Variable.value,
                            onCheckedChange = {mutableState_Variable.value = !mutableState_Variable.value
                                OnToggle( mutableState_Variable.value )},
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                            )
                        )
                    },
                    modifier = Modifier.clickable {
                        mutableState_Variable.value = !mutableState_Variable.value
                        OnToggle( mutableState_Variable.value )
                    }
                )
            }
        }


        fun LazyListScope.SwitchItemWithIcon(
            text: Int,
            description: Int,
            mutableState_Variable: MutableState<Boolean>,
            icon: ImageVector = DefaultIcon(),
            OnToggle: (Boolean) -> Unit
        ) {
            item {
                ListItem(
                    headlineContent = {if (text != 0) Text(stringResource(text))},
                    supportingContent = { if (description != 0) Text(stringResource(description))},
                    leadingContent = {Icon(icon, contentDescription = null)},
                    trailingContent = {
                        Switch(
                            checked = mutableState_Variable.value,
                            onCheckedChange = {mutableState_Variable.value = !mutableState_Variable.value
                                OnToggle( mutableState_Variable.value )},
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                            )
                        )
                    },
                    modifier = Modifier.clickable {
                        mutableState_Variable.value = !mutableState_Variable.value
                        OnToggle( mutableState_Variable.value )
                    }
                )
            }
        }


        fun LazyListScope.ButtonItem(
            text: Int,
            description: Int,
            onClick: () -> Unit
        ) {
            item {
                ListItem(
                    headlineContent = {if (text != 0) Text(stringResource(text))},
                    supportingContent = { if (description != 0) Text(stringResource(description))},
                    modifier = Modifier.clickable {
                        onClick()
                    }
                )
            }
        }


        fun LazyListScope.ButtonItemWithIcon(
            text: Int,
            description: Int,
            icon: ImageVector = DefaultIcon(),
            onClick: () -> Unit
        ) {
            item {
                ListItem(
                    headlineContent = {if (text != 0) Text(stringResource(text))},
                    leadingContent = {Icon(icon, contentDescription = null)},
                    supportingContent = { if (description != 0) Text(stringResource(description))},
                    modifier = Modifier.clickable {
                        onClick()
                    }
                )
            }
        }


        fun LazyListScope.DividerH() {
            item {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Gray)
                        .zIndex(1f),
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 0.5.dp
                )
            }
        }


        fun LazyListScope.ContentHeader(
            text: Int,
            modifier: Modifier = Modifier.padding(4.dp)
        ){
            item {
                Spacer(modifier = modifier)
                ListItem(
                    headlineContent = {
                        Text(
                            stringResource(text),
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                )
            }
        }


        fun LazyListScope.SegmentedRowItem(
            array: Int,
            mutableState_Variable: MutableIntState,
            OnChange: (Int) -> Unit
        ) {
            item {
                val options = stringArrayResource(array)
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
                            onCheckedChange = {
                                mutableState_Variable.intValue = index
                                OnChange(mutableState_Variable.intValue)
                            },
                            checked = index == mutableState_Variable.intValue
                        ) {
                            Text(text = label, softWrap = true, maxLines = 1)
                        }
                    }
                }
            }
        }


        fun LazyListScope.TextWithStringHeaderAndDescription(
            title: String,
            description: String,
            bottomDivider: Boolean = false
        ) {
            item {
                ListItem(
                    headlineContent = {
                        Text(title)
                    },
                    supportingContent = {
                        Text(description)
                    }
                )
                if (bottomDivider) this@TextWithStringHeaderAndDescription.DividerH()

            }
        }

        fun LazyListScope.FoldedTabs(
            navigator: cafe.adriel.voyager.navigator.Navigator,
            inRoot: Boolean = true
        ) {
            if (App.ShowHome() == 1) {
                ButtonItemWithIcon(R.string.tabs_label_home, 0, Icons.Filled.Home) {
                    if (inRoot) {
                        navigator.parent?.parent?.push(HomeScreen())
                    } else {
                        navigator.push(HomeScreen())
                    }
                }
            }
            if (App.ShowFriends() == 1) {
                ButtonItemWithIcon(R.string.tabs_label_friends, 0, Icons.Filled.Person) {
                    if (inRoot) {
                        navigator.parent?.parent?.push(FriendsScreen())
                    } else {
                        navigator.push(FriendsScreen())
                    }
                }
            }
            if (App.ShowFavorites() == 1) {
                ButtonItemWithIcon(R.string.tabs_label_favorites, 0, Icons.Filled.Star) {
                    if (inRoot) {
                        navigator.parent?.parent?.push(FavoritesScreen())
                    } else {
                        navigator.push(FavoritesScreen())
                    }
                }
            }
            if (App.ShowFeed() == 1) {
                ButtonItemWithIcon(R.string.tabs_label_feed, 0, Icons.Filled.BarChart) {
                    if (inRoot) {
                        navigator.parent?.parent?.push(FeedScreen())
                    } else {
                        navigator.push(FeedScreen())
                    }
                }
            }
            if (App.ShowHome() == 1 || App.ShowFriends() == 1 || App.ShowFavorites() == 1 || App.ShowFeed() == 1) {
                this@FoldedTabs.DividerH()
            }
        }
    } //END OF COMPANION OBJECT
}