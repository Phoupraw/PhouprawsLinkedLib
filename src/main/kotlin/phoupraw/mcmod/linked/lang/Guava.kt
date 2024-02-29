package phoupraw.mcmod.linked.lang

import com.google.common.collect.Table

inline fun <R, C, V : Any> Table<R, C, V>.getOrPut(rowKey: R, columnKey: C, getValue: () -> V) = get(rowKey, columnKey) ?: getValue().also { put(rowKey, columnKey, it) }
operator fun <R, C, V> Table<R, C, V>.set(rowKey: R, columnKey: C, value: V) = put(rowKey, columnKey, value)
//inline fun <R, C, V> Table<R, C, V>.removeIf(shouldRemove:(rowKey: R, columnKey: C)->Boolean):Boolean {
//    var changed = false
//    cellSet()
//    return changed
//}