package phoupraw.mcmod.linked.minecraft

import net.minecraft.nbt.*
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import phoupraw.mcmod.linked.lang.removeWhen

fun NbtVarInt(value: Int): AbstractNbtNumber = when (value) {
    in Byte.MIN_VALUE..Byte.MAX_VALUE -> NbtByte.of(value.toByte())
    in Short.MIN_VALUE..Short.MAX_VALUE -> NbtShort.of(value.toShort())
    else -> NbtInt.of(value)
}

fun NbtVarInt(value: Long): AbstractNbtNumber = when (value) {
    in Int.MIN_VALUE..Int.MAX_VALUE -> NbtVarInt(value.toInt())
    else -> NbtLong.of(value)
}

operator fun NbtCompound?.plus(other: NbtCompound?): NbtCompound? = this?.run { other?.let { copyFrom(it) } ?: copy() } ?: other?.copy()
fun NbtElement.clean(): NbtElement? = when (this) {
    is NbtCompound -> clean()
    is AbstractNbtList<*> -> clean()
    is AbstractNbtNumber -> clean()
    is NbtString -> clean()
    else -> null
}

fun NbtCompound.clean(): NbtCompound? {
    keys.removeWhen { get(it)?.clean() == null }
    return takeUnless { isEmpty }
}

fun <T : NbtElement> AbstractNbtList<T>.clean(): AbstractNbtList<T>? {
    removeWhen { it.clean() == null }
    return takeUnless { isEmpty() }
}

fun NbtString.clean(): NbtString? = takeIf { asString().isEmpty() }
fun AbstractNbtNumber.clean(): AbstractNbtNumber? = when (this) {
    is NbtByte -> clean()
    is NbtShort -> clean()
    is NbtInt -> clean()
    is NbtLong -> clean()
    is NbtFloat -> clean()
    is NbtDouble -> clean()
    else -> null
}

fun NbtByte.clean(): NbtByte? = takeIf { byteValue() != 0.toByte() }
fun NbtShort.clean(): NbtShort? = takeIf { shortValue() != 0.toShort() }
fun NbtInt.clean(): NbtInt? = takeIf { intValue() != 0 }
fun NbtLong.clean(): NbtLong? = takeIf { longValue() != 0L }
fun NbtFloat.clean(): NbtFloat? = takeIf { floatValue() != 0f }
fun NbtDouble.clean(): NbtDouble? = takeIf { doubleValue() != 0.0 }
fun NbtCompound.getOrDefault(key: String, value: Long) = getOrDefault(key) { value }
inline fun NbtCompound.getOrDefault(key: String, get: () -> Long): Long = if (contains(key, NbtElement.NUMBER_TYPE.toInt())) getLong(key) else get()
fun NbtCompound.putVarInt(key: String, value: Int) = put(key, NbtVarInt(value))
fun NbtCompound.putVarInt(key: String, value: Long) = put(key, NbtVarInt(value))
fun NbtCompound.getText(key: String): Text? = if (contains(key, NbtElement.STRING_TYPE.toInt())) Text.Serializer.fromJson(getString(key)) else null
fun NbtCompound.put(key: String, text: Text?) = set(key, text?.let { NbtString.of(Text.Serializer.toJson(it)) })
fun NbtCompound.getId(key: String): Identifier? = if (contains(key, NbtElement.STRING_TYPE.toInt())) Identifier(getString(key)) else null
fun NbtCompound.put(key: String, id: Identifier?) = set(key, id?.run { NbtString.of(toString()) })
var NbtCompound.customName: Text?
    get() = getText("CustomName")
    set(value) = put("CustomName", value)

fun NbtCompound.get(path: List<String>): NbtElement? {
    var node: NbtCompound = this
    val iterator = path.iterator()
    while (iterator.hasNext()) {
        val nodeKey = iterator.next()
        if (!iterator.hasNext()) {
            return node[nodeKey]
        }
        if (!node.contains(nodeKey, NbtElement.COMPOUND_TYPE.toInt())) {
            break
        }
        node = node.getCompound(nodeKey)
    }
    return null
}

fun NbtCompound.put(path: List<String>, value: NbtElement?) {
    var node: NbtCompound = this
    val iterator = path.iterator()
    while (iterator.hasNext()) {
        val nodeKey = iterator.next()
        if (!iterator.hasNext()) {
            node[nodeKey] = value
        } else {
            var nextNode: NbtCompound
            if (node.contains(nodeKey, NbtElement.COMPOUND_TYPE.toInt())) {
                nextNode = node.getCompound(nodeKey)
            } else {
                nextNode = NbtCompound()
                node[nodeKey] = nextNode
            }
            node = nextNode
        }
    }
}

operator fun NbtCompound.set(key: String, value: NbtElement?) {
    if (value == null) {
        remove(key)
    } else {
        put(key, value)
    }
}

fun NbtCompound.getLongOrNull(key: String) = getLong(key).takeIf { contains(key, NbtElement.NUMBER_TYPE.toInt()) }
