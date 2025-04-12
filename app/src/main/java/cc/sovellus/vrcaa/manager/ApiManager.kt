package cc.sovellus.vrcaa.manager

import android.annotation.SuppressLint
import cc.sovellus.vrcaa.api.vrchat.http.HttpClient
import cc.sovellus.vrcaa.base.BaseManager

@SuppressLint("StaticFieldLeak")
object ApiManager : BaseManager<Any>() {
    var api = HttpClient()
}