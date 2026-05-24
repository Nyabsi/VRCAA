package cc.sovellus.vrcaa.ui.components.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


const val SEARCH_FILTER_MIN_COUNT = 5
const val SEARCH_FILTER_MAX_COUNT = 100
const val SEARCH_FILTER_SNAP_STEP = 5

@Composable
fun SnappedCountSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int = SEARCH_FILTER_MIN_COUNT,
    max: Int = SEARCH_FILTER_MAX_COUNT,
    step: Int = SEARCH_FILTER_SNAP_STEP
) {
    val normalizedValue = snapCountValue(value, min, max, step)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = normalizedValue.toString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = normalizedValue.toFloat(),
            onValueChange = { onValueChange(snapCountValue(it, min, max, step)) },
            valueRange = min.toFloat()..max.toFloat(),
            steps = ((max - min) / step).coerceAtLeast(1) - 1
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = min.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = max.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun snapCountValue(
    value: Int,
    min: Int,
    max: Int,
    step: Int
): Int {
    return snapCountValue(value.toFloat(), min, max, step)
}

private fun snapCountValue(
    value: Float,
    min: Int,
    max: Int,
    step: Int
): Int {
    val clamped = value.coerceIn(min.toFloat(), max.toFloat())
    val snappedSteps = ((clamped - min) / step).roundToInt()
    return (min + snappedSteps * step).coerceIn(min, max)
}