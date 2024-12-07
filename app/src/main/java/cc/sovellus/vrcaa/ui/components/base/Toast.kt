package cc.sovellus.vrcaa.ui.components.base

import android.content.Context
import android.widget.Toast

fun quickToast(context: Context, text: Any) {
    val string = if (text is Int) context.getString(text) else text.toString()
    Toast.makeText(
        context,
        string,
        Toast.LENGTH_SHORT
    ).show()
}
