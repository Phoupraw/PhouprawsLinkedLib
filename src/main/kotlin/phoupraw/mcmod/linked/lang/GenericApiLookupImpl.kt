@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.lang

@Deprecated("与GenericApiLookup一同废弃", ReplaceWith("SingleApiLookupImpl"))
open class GenericApiLookupImpl<K, A, C>(@get:JvmName("name") override val name: String) : GenericApiProvider<K, A, C>, GenericApiLookup<K, A, C> {
    @get:JvmName("preliminary")
    override val preliminary: MutableEvent<GenericApiProvider<K, A, C>> = newEvent()
    @get:JvmName("fallback")
    override val fallback: MutableEvent<GenericApiProvider<K, A, C>> = newEvent()
    @get:JvmName("keySpecific")
    override val keySpecific: Map<K, MutableEvent<GenericApiProvider<K, A, C>>> get() = _keySpecific
    private val _keySpecific: MutableMap<K, MutableEvent<GenericApiProvider<K, A, C>>> = mutableMapOf()
    @Suppress("UNCHECKED_CAST")
    override operator fun <T : K> get(key: T): MutableEvent<GenericApiProvider<T, A, C>> = _keySpecific.getOrPut(key) { newEvent() } as MutableEvent<GenericApiProvider<T, A, C>>
    override operator fun invoke(key: K, context: C): A? {
        preliminary.call(key, context)?.also { return it }
        keySpecific[key]?.call?.invoke(key, context)?.also { return it }
        fallback.call(key, context)?.also { return it }
        return null
    }

    fun <T : K> newEvent(): MutableEvent<GenericApiProvider<T, A, C>> = MutableEvent { finds ->
        { key, context -> finds.firstNotNullOfOrNull { it(key, context) } }
    }
    //        return EventFactory.createArrayBacked(GenericApiProvider::class.java) { finds ->
    //            { key, context -> finds.firstNotNullOfOrNull { it(key, context) } }
    //        }
}