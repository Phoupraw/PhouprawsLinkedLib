@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.api.util

import com.google.common.collect.Multimap
import org.jetbrains.annotations.UnmodifiableView
import phoupraw.mcmod.linked.impl.util.MutableEventImpl

interface MutableEvent<T> {
    @get:JvmName("call")
    val call: T
    @get:JvmName("callbacks")
    val callbacks: @UnmodifiableView Multimap<String, T>
    fun register(callback: T) = register(DEFAULT_KEY, callback)
    fun register(key: String, callback: T)
    /**
     * 移除[key]下的所有回调
     */
    fun remove(key: String): Collection<T>
    /**
     * 移除[key]下的[callback]
     */
    //@JvmOverloads
    fun remove(callback: T, key: String = DEFAULT_KEY): Boolean
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