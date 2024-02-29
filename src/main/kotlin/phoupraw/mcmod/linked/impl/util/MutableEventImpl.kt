package phoupraw.mcmod.linked.impl.util

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import com.google.common.collect.Multimaps
import org.jetbrains.annotations.UnmodifiableView
import phoupraw.mcmod.linked.api.util.MutableEvent

abstract class MutableEventImpl<T> : MutableEvent<T>, Comparator<String> {
    var sorted = true
    override val call: T
        get() {
            if (!sorted) {
                synchronized(this) {
                    if (sorted) return@synchronized
                    val sortedMap = MultimapBuilder.treeKeys(this).arrayListValues().build(_callbacks)
                    _callbacks.clear()
                    _callbacks.putAll(sortedMap)
                    sorted = true
                }
            }
            return _call
        }
    abstract val _call: T
    val orders: Multimap<String, String> = Multimaps.synchronizedMultimap(MultimapBuilder.hashKeys().hashSetValues().build())
    val _callbacks: Multimap<String, T> = Multimaps.synchronizedMultimap(MultimapBuilder.linkedHashKeys().arrayListValues().build())
    override val callbacks: @UnmodifiableView Multimap<String, T> = Multimaps.unmodifiableMultimap(_callbacks)
    override fun register(key: String, callback: T) {
        _callbacks.put(key, callback)
    }

    override fun remove(key: String): Collection<T> = _callbacks.removeAll(key)
    override fun remove(callback: T, key: String): Boolean = _callbacks.remove(key, callback)
    override fun addKeyOrder(keyFirst: String, keySecond: String) {
        require(keyFirst != keySecond) { "$keySecond == $keyFirst" }
        require(keyFirst !in orders[keySecond]) { "$keyFirst < $keySecond" }
        orders.put(keyFirst, keySecond)
        sorted = false
    }

    override fun compare(o1: String, o2: String): Int = when {
        o2 in orders[o1] -> -1
        o1 in orders[o2] -> 1
        else -> 0
    }
}