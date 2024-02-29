package phoupraw.mcmod.linked.lang

abstract class DefaultedMap<K, V>(val back: MutableMap<K, V>) : MutableMap<K, V> by back, (K) -> V {
    abstract override fun invoke(key: K): V
    override fun get(key: K): V = back.getOrPut(key) { this(key) }

    companion object {
        fun <K, V> constant(back: MutableMap<K, V>, value: V): DefaultedMap<K, V> = invoke(back, value)
        operator fun <K, V> invoke(back: MutableMap<K, V>, value: V): DefaultedMap<K, V> = object : DefaultedMap<K, V>(back) {
            override fun invoke(key: K): V = value
        }

        inline fun <K, V> supplier(back: MutableMap<K, V>, crossinline getValue: () -> V): DefaultedMap<K, V> = invoke(back, getValue)
        inline operator fun <K, V> invoke(back: MutableMap<K, V>, crossinline getValue: () -> V): DefaultedMap<K, V> = object : DefaultedMap<K, V>(back) {
            override fun invoke(key: K): V = getValue()
        }

        inline fun <K, V> function(back: MutableMap<K, V>, crossinline getValue: (key: K) -> V): DefaultedMap<K, V> = invoke(back, getValue)
        inline operator fun <K, V> invoke(back: MutableMap<K, V>, crossinline getValue: (key: K) -> V): DefaultedMap<K, V> = object : DefaultedMap<K, V>(back) {
            override fun invoke(key: K): V = getValue(key)
        }
    }
}