package cc.sovellus.vrcaa.manager

import android.annotation.SuppressLint
import cc.sovellus.vrcaa.api.vrchat.http.HttpClient

@SuppressLint("StaticFieldLeak")
object ApiManager {
    var api = HttpClient()
}