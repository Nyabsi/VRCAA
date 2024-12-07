package cc.sovellus.vrcaa.ui.components.base

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp

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