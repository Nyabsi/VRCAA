/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.ui.components.input

import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.App

@Composable
fun CodeInput(
    input: MutableState<String>
) {
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        value = input.value,
        onValueChange = {
            if (it.length <= 6)
                input.value = it
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword
        ),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement
                    .spacedBy(
                        space = 6.dp,
                        alignment = Alignment.CenterHorizontally
                    )
            ) {
                repeat(6) {
                    val char = when {
                        it >= input.value.length -> ""
                        else -> input.value[it].toString()
                    }

                    Text(
                        modifier = Modifier
                            .width(50.dp)
                            .height(60.dp)
                            .border(
                                1.dp, if (App.isAppInDarkTheme()) {
                                    Color.White
                                } else {
                                    Color.Black
                                }, RoundedCornerShape(8.dp)
                            )
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        text = char,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            }
        }
    )
}