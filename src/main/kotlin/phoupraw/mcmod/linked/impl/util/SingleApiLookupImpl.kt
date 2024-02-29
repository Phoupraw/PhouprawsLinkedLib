@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.impl.util

import phoupraw.mcmod.linked.api.util.MutableEvent
import phoupraw.mcmod.linked.api.util.SingleApiFinder
import phoupraw.mcmod.linked.api.util.SingleApiLookup

open class SingleApiLookupImpl<K, C, A>(override val name: String) : SingleApiLookup<K, C, A> {
    override val preliminary: MutableEvent<SingleApiFinder<K, C, A>> get() = newEvent()
    override val keySpecific: Map<K, MutableEvent<SingleApiFinder<K, C, A>>> get() = _keySpecific
    @get:JvmName("_keySpecific")
    val _keySpecific: MutableMap<K, MutableEvent<SingleApiFinder<K, C, A>>> = mutableMapOf()
    override val fallback: MutableEvent<SingleApiFinder<K, C, A>> get() = newEvent()
    override var find: SingleApiFinder<K, C, A> = SingleApiFinder(::defaultFind)
    @Suppress("UNCHECKED_CAST")
    override fun <T : K> get(key: T): MutableEvent<SingleApiFinder<T, C, A>> = _keySpecific.getOrPut(key) { newEvent() } as MutableEvent<SingleApiFinder<T, C, A>>
    fun defaultFind(key: K, context: C): A? = preliminary.call(key, context) ?: keySpecific[key]?.call?.invoke(key, context) ?: fallback.call(key, context)
    fun <T : K> newEvent(): MutableEvent<SingleApiFinder<T, C, A>> = MutableEvent { finds ->
        SingleApiFinder { key, context -> finds.firstNotNullOfOrNull { it(key, context) } }
    }
}