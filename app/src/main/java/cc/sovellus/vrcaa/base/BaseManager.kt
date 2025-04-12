package cc.sovellus.vrcaa.base

open class BaseManager<T> {

    private var listeners: MutableList<T> = mutableListOf()

    fun addListener(listener: T) {
        listeners.add(listener)
    }

    fun getListeners(): MutableList<T> {
        return listeners
    }
}