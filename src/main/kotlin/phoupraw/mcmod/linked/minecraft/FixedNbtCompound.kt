package phoupraw.mcmod.linked.minecraft

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import java.util.*

open class FixedNbtCompound(entries: Map<String, NbtElement>) : NbtCompound(Collections.unmodifiableMap(entries)) {
    constructor(copyFrom: NbtCompound) : this(copyFrom.run {
        val entries = mutableMapOf<String, NbtElement>()
        for (key in keys) this[key]?.also { entries[key] = it }
        entries
    })

    override fun put(key: String?, element: NbtElement?): NbtElement? = null
    override fun putByte(key: String?, value: Byte) = Unit
    override fun putShort(key: String?, value: Short) = Unit
    override fun putBoolean(key: String?, value: Boolean) = Unit
    override fun putByteArray(key: String?, value: ByteArray?) = Unit
    override fun putByteArray(key: String?, value: MutableList<Byte>?) = Unit
    override fun putDouble(key: String?, value: Double) = Unit
    override fun putFloat(key: String?, value: Float) = Unit
    override fun putInt(key: String?, value: Int) = Unit
    override fun putIntArray(key: String?, value: IntArray?) = Unit
    override fun putIntArray(key: String?, value: MutableList<Int>?) = Unit
    override fun putLong(key: String?, value: Long) = Unit
    override fun putLongArray(key: String?, value: LongArray?) = Unit
    override fun putLongArray(key: String?, value: MutableList<Long>?) = Unit
    override fun putString(key: String?, value: String?) = Unit
    override fun putUuid(key: String?, value: UUID?) = Unit
    override fun copyFrom(source: NbtCompound?): NbtCompound = this

    companion object Empty : FixedNbtCompound(emptyMap())
}