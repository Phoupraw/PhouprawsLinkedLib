package phoupraw.mcmod.linked.lang

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import com.google.common.collect.Multimaps

@Deprecated("与phoupraw.mcmod.linked.lang.MutableEvent一同废弃", ReplaceWith("phoupraw.mcmod.linked.impl.lang.MutableEventImpl"))
abstract class MutableEventImpl<T> : MutableEvent<T>, Comparator<String> {
    var sorted = true
    override val call: T
        get() {
            if (!sorted) {
                synchronized(this) {
                    if (sorted) return@synchronized
                    val sortedMap = MultimapBuilder.treeKeys(this).arrayListValues().build(callbacks)
                    callbacks.clear()
                    callbacks.putAll(sortedMap)
                    sorted = true
                }
            }
            return _call
        }
    abstract val _call: T
    val orders: Multimap<String, String> = Multimaps.synchronizedMultimap(MultimapBuilder.hashKeys().hashSetValues().build())
    override val callbacks: Multimap<String, T> = Multimaps.synchronizedMultimap(MultimapBuilder.linkedHashKeys().arrayListValues().build())
    override fun register(key: String, callback: T) {
        callbacks.put(key, callback)
    }

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