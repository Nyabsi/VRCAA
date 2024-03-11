package cc.sovellus.vrcaa.ui.components.misc

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R

@Composable
fun Languages(languages: List<String>) {
    Row(
        modifier = Modifier.padding(start = 24.dp)
    ) {
        var wasEvenOneFoundFlag = false
        languages.let {
            for (language in languages) {
                if (language.contains("language_")) {
                    if (!wasEvenOneFoundFlag)
                        wasEvenOneFoundFlag = true

                    when (language.substring("language_".length)) {
                        "eng" -> Image(
                            painter = painterResource(R.drawable.flag_gb),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "kor" -> Image(
                            painter = painterResource(R.drawable.flag_kr),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "rus" -> Image(
                            painter = painterResource(R.drawable.flag_ru),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "spa" -> Image(
                            painter = painterResource(R.drawable.flag_es),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "por" -> Image(
                            painter = painterResource(R.drawable.flag_br),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "zho" -> Image(
                            painter = painterResource(R.drawable.flag_cn),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "deu" -> Image(
                            painter = painterResource(R.drawable.flag_de),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "jpn" -> Image(
                            painter = painterResource(R.drawable.flag_jp),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "fra" -> Image(
                            painter = painterResource(R.drawable.flag_fr),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "swe" -> Image(
                            painter = painterResource(R.drawable.flag_se),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "nld" -> Image(
                            painter = painterResource(R.drawable.flag_nl),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "pol" -> Image(
                            painter = painterResource(R.drawable.flag_pl),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "dan" -> Image(
                            painter = painterResource(R.drawable.flag_dk),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "nor" -> Image(
                            painter = painterResource(R.drawable.flag_no),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "ita" -> Image(
                            painter = painterResource(R.drawable.flag_it),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "tha" -> Image(
                            painter = painterResource(R.drawable.flag_th),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "fin" -> Image(
                            painter = painterResource(R.drawable.flag_fi),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "hun" -> Image(
                            painter = painterResource(R.drawable.flag_hu),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "ces" -> Image(
                            painter = painterResource(R.drawable.flag_cz),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "tur" -> Image(
                            painter = painterResource(R.drawable.flag_tr),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "ara" -> Image(
                            painter = painterResource(R.drawable.flag_ar),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "ron" -> Image(
                            painter = painterResource(R.drawable.flag_ro),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "vie" -> Image(
                            painter = painterResource(R.drawable.flag_vn),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "ukr" -> Image(
                            painter = painterResource(R.drawable.flag_ua),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "ase" -> Image(
                            painter = painterResource(R.drawable.flag_us),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "bfi" -> Image(
                            painter = painterResource(R.drawable.flag_gb),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "dse" -> Image(
                            painter = painterResource(R.drawable.flag_de),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "fsl" -> Image(
                            painter = painterResource(R.drawable.flag_fr),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "jsl" -> Image(
                            painter = painterResource(R.drawable.flag_jp),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )

                        "kvk" -> Image(
                            painter = painterResource(R.drawable.flag_kr),
                            contentDescription = "User Language flag",
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }
            }
        }
        if (!wasEvenOneFoundFlag) {
            Text(stringResource(R.string.profile_text_no_languages))
        }
    }
}