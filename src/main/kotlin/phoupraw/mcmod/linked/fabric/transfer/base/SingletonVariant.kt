@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.fabric.transfer.base

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import phoupraw.mcmod.linked.minecraft.FixedNbtCompound

open class SingletonVariant : TransferVariant<Unit> {
    override val blank: Boolean @JvmName("isBlank") get() = false
    override val `object`: Unit get() {}
    override val nbt: NbtCompound? get() = null
    override fun hasNbt(): Boolean = false
    override fun nbtMatches(other: NbtCompound?): Boolean = true
    override fun isOf(`object`: Unit): Boolean = true
    override fun copyNbt(): NbtCompound? = null
    override fun copyOrCreateNbt(): NbtCompound = FixedNbtCompound.Empty
    override fun toNbt(): NbtCompound = FixedNbtCompound.Empty
    override fun toPacket(buf: PacketByteBuf) {}
    override fun toString(): String = javaClass.getSimpleName()
}
