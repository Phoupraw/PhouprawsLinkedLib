@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.lang

@Deprecated("与GenericApiLookup一同废弃", ReplaceWith("SingleApiFinder"))
typealias GenericApiProvider<K, A, C> = (key: K, context: C) -> A?

@Deprecated("invoke这个方法名不好", ReplaceWith("SingleApiLookup"))
interface GenericApiLookup<K, A, C> {
    @get:JvmName("name")
    val name: String
    @get:JvmName("preliminary")
    val preliminary: MutableEvent<GenericApiProvider<K, A, C>>
    @get:JvmName("fallback")
    val fallback: MutableEvent<GenericApiProvider<K, A, C>>
    @get:JvmName("keySpecific")
    val keySpecific: Map<K, MutableEvent<GenericApiProvider<K, A, C>>>
    operator fun <T : K> get(key: T): MutableEvent<GenericApiProvider<T, A, C>>
    fun <T : K> register(vararg keys: T, provider: GenericApiProvider<T, A, C>) {
        for (key in keys) {
            this[key].register(provider)
        }
    }

    operator fun invoke(key: K, context: C): A?

    companion object {
        @JvmStatic
        operator fun <K, A, C> invoke(name: String): GenericApiLookup<K, A, C> = GenericApiLookupImpl<K, A, C>(name)
        @JvmStatic
        inline fun <K, A, C> providerOf(crossinline find: (context: C) -> A?): GenericApiProvider<K, A, C> = { _, context -> find(context) }
    }
}