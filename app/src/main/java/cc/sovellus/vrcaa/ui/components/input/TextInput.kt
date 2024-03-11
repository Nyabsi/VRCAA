package cc.sovellus.vrcaa.ui.components.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextInput(
    title: String,
    input: MutableState<String>
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        value = input.value,
        onValueChange = { input.value = it },
        label = { Text(text = title) },
        singleLine = true,
    )
}