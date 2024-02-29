package phoupraw.mcmod.linked.api.util

fun interface SingleApiFinder<in K, in C, out A> : (K, C) -> A? {
    override fun invoke(key: K, context: C): A?

    companion object {
        @JvmStatic
        operator fun <K, C, A> invoke(find: (context: C) -> A?): SingleApiFinder<K, C, A> = SingleApiFinder { _, context -> find(context) }
    }
}