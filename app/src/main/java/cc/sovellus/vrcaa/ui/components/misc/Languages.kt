package cc.sovellus.vrcaa.ui.components.misc

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cc.sovellus.vrcaa.R

@Composable
fun Languages(languages: List<String>, isGroup: Boolean = false, @SuppressLint("ModifierParameter") modifier: Modifier? = null) {
    Row(
        modifier = modifier ?: Modifier.padding(start = 12.dp)
    ) {
        languages.let {
            for (language in languages) {
                if (language.contains("language_") or isGroup) {
                    when (if (isGroup) { language } else { language.substring("language_".length) }) {
                        "eng" -> Image(
                            painter = painterResource(R.drawable.flag_gb),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                            alignment = Alignment.CenterEnd
                        )

                        "kor" -> Image(
                            painter = painterResource(R.drawable.flag_kr),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "rus" -> Image(
                            painter = painterResource(R.drawable.flag_ru),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "spa" -> Image(
                            painter = painterResource(R.drawable.flag_es),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "por" -> Image(
                            painter = painterResource(R.drawable.flag_br),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "zho" -> Image(
                            painter = painterResource(R.drawable.flag_cn),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "deu" -> Image(
                            painter = painterResource(R.drawable.flag_de),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "jpn" -> Image(
                            painter = painterResource(R.drawable.flag_jp),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                            alignment = Alignment.CenterEnd
                        )

                        "fra" -> Image(
                            painter = painterResource(R.drawable.flag_fr),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "swe" -> Image(
                            painter = painterResource(R.drawable.flag_se),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "nld" -> Image(
                            painter = painterResource(R.drawable.flag_nl),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "pol" -> Image(
                            painter = painterResource(R.drawable.flag_pl),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "dan" -> Image(
                            painter = painterResource(R.drawable.flag_dk),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "nor" -> Image(
                            painter = painterResource(R.drawable.flag_no),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "ita" -> Image(
                            painter = painterResource(R.drawable.flag_it),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "tha" -> Image(
                            painter = painterResource(R.drawable.flag_th),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "fin" -> Image(
                            painter = painterResource(R.drawable.flag_fi),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "hun" -> Image(
                            painter = painterResource(R.drawable.flag_hu),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "ces" -> Image(
                            painter = painterResource(R.drawable.flag_cz),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "tur" -> Image(
                            painter = painterResource(R.drawable.flag_tr),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "ara" -> Image(
                            painter = painterResource(R.drawable.flag_ar),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "ron" -> Image(
                            painter = painterResource(R.drawable.flag_ro),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "vie" -> Image(
                            painter = painterResource(R.drawable.flag_vn),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "ukr" -> Image(
                            painter = painterResource(R.drawable.flag_ua),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "ase" -> Image(
                            painter = painterResource(R.drawable.flag_us),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "bfi" -> Image(
                            painter = painterResource(R.drawable.flag_gb),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "dse" -> Image(
                            painter = painterResource(R.drawable.flag_de),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "fsl" -> Image(
                            painter = painterResource(R.drawable.flag_fr),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "jsl" -> Image(
                            painter = painterResource(R.drawable.flag_jp),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )

                        "kvk" -> Image(
                            painter = painterResource(R.drawable.flag_kr),
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )
                    }
                }
            }
        }
    }
}