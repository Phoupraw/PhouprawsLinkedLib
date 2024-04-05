package phoupraw.mcmod.linked.lang

import kotlin.reflect.KClass

/**
 * 傻逼[MutableIterable.removeAll]不是内联
 */
inline fun <T> MutableIterable<T>.removeWhen(shouldRemove: (T) -> Boolean): Boolean {
    var modified = false
    val iter = iterator()
    while (iter.hasNext()) {
        if (shouldRemove(iter.next())) {
            iter.remove()
            modified = true
        }
    }
    return modified
}

inline fun <reified T : Any> KClass(): KClass<T> = T::class
operator fun Int?.plus(value: Int) = this?.plus(value) ?: value
operator fun Long?.plus(value: Long) = this?.plus(value) ?: value
operator fun Long?.plus(value: Int) = this?.plus(value) ?: value
operator fun Double?.plus(value: Int) = this?.plus(value) ?: value
operator fun Double?.plus(value: Double) = this?.plus(value) ?: value
fun Boolean.toInt() = if (this) 1 else 0
fun Int.toBoolean() = this != 0