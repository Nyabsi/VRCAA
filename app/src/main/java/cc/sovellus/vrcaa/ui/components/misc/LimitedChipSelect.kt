package cc.sovellus.vrcaa.ui.components.misc

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LimitedChipSelect(
    items: List<String>,
    selected: List<String>,
    minSelected: Int,
    maxSelected: Int,
    onSelectedChange: (List<String>) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FlowRow() {
            items.forEach { item ->

                val isSelected = item in selected

                FilterChip(
                    modifier = Modifier.padding(start = 4.dp),
                    selected = isSelected,
                    onClick = {
                        val newSelection = when {
                            !isSelected && selected.size < maxSelected ->
                                selected + item

                            isSelected && selected.size > minSelected ->
                                selected - item

                            else -> selected
                        }

                        onSelectedChange(newSelection)
                    },
                    label = { Text(item) }
                )
            }
        }
    }
}