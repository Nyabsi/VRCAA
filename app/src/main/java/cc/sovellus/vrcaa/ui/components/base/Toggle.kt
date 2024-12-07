package cc.sovellus.vrcaa.ui.components.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

fun LazyListScope.toggle(
    text: Int,
    description: Int,
    mutableState_Variable: MutableState<Boolean>,
    OnToggle: (Boolean) -> Unit
) {
    item {
        ListItem(
            headlineContent = { if (text != 0) Text(stringResource(text)) },
            supportingContent = { if (description != 0) Text(stringResource(description)) },
            trailingContent = {
                Switch(
                    checked = mutableState_Variable.value,
                    onCheckedChange = {
                        mutableState_Variable.value = !mutableState_Variable.value
                        OnToggle(mutableState_Variable.value)
                    },
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
                OnToggle(mutableState_Variable.value)
            }
        )
    }
}