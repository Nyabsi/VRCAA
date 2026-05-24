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

package cc.sovellus.vrcaa.ui.components.misc

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import cc.sovellus.vrcaa.R

private val languageFlags = mapOf(
    "afr" to R.drawable.flag_za,
    "ara" to R.drawable.flag_ar,
    "ben" to R.drawable.flag_bd,
    "bul" to R.drawable.flag_bg,
    "ces" to R.drawable.flag_cz,
    "cmn" to R.drawable.flag_cn,
    "cym" to R.drawable.flag_wales,
    "dan" to R.drawable.flag_dk,
    "deu" to R.drawable.flag_de,
    "ell" to R.drawable.flag_gr,
    "eng" to R.drawable.flag_gb,
    "epo" to R.drawable.flag_epo,
    "est" to R.drawable.flag_ee,
    "fil" to R.drawable.flag_ph,
    "fin" to R.drawable.flag_fi,
    "fra" to R.drawable.flag_fr,
    "gla" to R.drawable.flag_scotland,
    "gle" to R.drawable.flag_ie,
    "heb" to R.drawable.flag_il,
    "hin" to R.drawable.flag_in,
    "hmn" to R.drawable.flag_unk,
    "hrv" to R.drawable.flag_hr,
    "hun" to R.drawable.flag_hu,
    "hye" to R.drawable.flag_am,
    "ind" to R.drawable.flag_id,
    "isl" to R.drawable.flag_is,
    "ita" to R.drawable.flag_it,
    "jpn" to R.drawable.flag_jp,
    "kor" to R.drawable.flag_kr,
    "lav" to R.drawable.flag_lv,
    "lit" to R.drawable.flag_lt,
    "ltz" to R.drawable.flag_lu,
    "mar" to R.drawable.flag_in,
    "mkd" to R.drawable.flag_mk,
    "mlt" to R.drawable.flag_mt,
    "mri" to R.drawable.flag_nz,
    "msa" to R.drawable.flag_my,
    "nld" to R.drawable.flag_nl,
    "nor" to R.drawable.flag_no,
    "pol" to R.drawable.flag_pl,
    "por" to R.drawable.flag_br,
    "ron" to R.drawable.flag_ro,
    "rus" to R.drawable.flag_ru,
    "sco" to R.drawable.flag_scotland,
    "slk" to R.drawable.flag_sk,
    "slv" to R.drawable.flag_si,
    "spa" to R.drawable.flag_es,
    "swe" to R.drawable.flag_se,
    "tel" to R.drawable.flag_in,
    "tha" to R.drawable.flag_th,
    "tok" to R.drawable.flag_tok,
    "tur" to R.drawable.flag_tr,
    "tws" to R.drawable.flag_cn,
    "ukr" to R.drawable.flag_ua,
    "vie" to R.drawable.flag_vn,
    "wuu" to R.drawable.flag_cn,
    "yue" to R.drawable.flag_hk,
    "zho" to R.drawable.flag_cn,
    "zxx" to R.drawable.flag_unk,

    "ase" to R.drawable.flag_us,
    "asf" to R.drawable.flag_au,
    "bfi" to R.drawable.flag_gb,
    "dse" to R.drawable.flag_nl,
    "fsl" to R.drawable.flag_fr,
    "gsg" to R.drawable.flag_de,
    "jsl" to R.drawable.flag_jp,
    "kvk" to R.drawable.flag_kr,
    "nzs" to R.drawable.flag_nz
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Languages(
    languages: List<String>,
    isGroup: Boolean = false,
    @SuppressLint("ModifierParameter")
    modifier: Modifier? = null
) {
    Row(
        modifier = modifier ?: Modifier.padding(start = 12.dp)
    ) {
        languages.forEach { language ->
            if (language.contains("language_") || isGroup) {

                val readableLanguage = if (isGroup) {
                    language
                } else {
                    language.removePrefix("language_")
                }

                val drawable = languageFlags[readableLanguage]
                    ?: R.drawable.flag_unk

                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip {
                            Text(readableLanguage.uppercase())
                        }
                    },
                    state = rememberTooltipState()
                ) {
                    Image(
                        painter = painterResource(drawable),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(2.dp)
                            .size(32.dp, 64.dp),
                        alignment = Alignment.CenterEnd
                    )
                }
            }
        }
    }
}