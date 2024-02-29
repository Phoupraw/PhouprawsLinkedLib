@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.api.util

import phoupraw.mcmod.linked.impl.util.SingleApiLookupImpl

interface SingleApiLookup<K, C, A> {
    @get:JvmName("name")
    val name: String
    @get:JvmName("preliminary")
    val preliminary: MutableEvent<SingleApiFinder<K, C, A>>
    operator fun <T : K> get(key: T): MutableEvent<SingleApiFinder<T, C, A>>
    fun <T : K> register(vararg keys: T, provider: SingleApiFinder<T, C, A>) {
        for (key in keys) {
            this[key].register(provider)
        }
    }
    @get:JvmName("keySpecific")
    val keySpecific: Map<K, MutableEvent<SingleApiFinder<K, C, A>>>
    @get:JvmName("fallback")
    val fallback: MutableEvent<SingleApiFinder<K, C, A>>
    @get:JvmName("find")
    var find: SingleApiFinder<K, C, A>

    companion object {
        @JvmStatic
        operator fun <K, C, A> invoke(name: String): SingleApiLookup<K, C, A> = SingleApiLookupImpl(name)
    }
}