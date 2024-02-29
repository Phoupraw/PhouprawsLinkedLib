@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.lang

import com.google.common.collect.Multimap

@Deprecated("callbacks在API中就可被修改，不太好", ReplaceWith("phoupraw.mcmod.linked.api.lang.MutableEvent"))
interface MutableEvent<T> {
    @get:JvmName("call")
    val call: T
    @get:JvmName("callbacks")
    val callbacks: Multimap<String, T>
    fun register(callback: T) = register(DEFAULT_KEY, callback)
    fun register(key: String, callback: T)
    fun addKeyOrder(keyFirst: String, keySecond: String)
    operator fun plusAssign(callback: T) = register(callback)

    companion object {
        const val DEFAULT_KEY = "default:default"
        inline operator fun <T> invoke(crossinline getCall: (callbacks: Iterable<T>) -> T): MutableEvent<T> {
            return object : MutableEventImpl<T>() {
                override val _call: T = getCall(callbacks.values())
            }
        }
    }
}