package phoupraw.mcmod.linked.fabric.transfer.base

import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage

open class CombinedSingleSlotStorage<T, S : SingleSlotStorage<T>>(parts: List<S>) : CombinedSlottedStorage<T, S>(parts) {
    override val slotCount: Int get() = parts.size
    override val slots: List<SingleSlotStorage<T>> get() = parts
    override fun getSlot(slot: Int): SingleSlotStorage<T> = parts[slot]
    override fun iterator(): Iterator<SingleSlotStorage<T>> = parts.iterator()
    override fun hashCode(): Int = parts.hashCode()
    override fun equals(other: Any?): Boolean = if (other is CombinedSlottedStorage<*, *>) parts == other.parts else super.equals(other)
    override fun toString(): String = javaClass.getSimpleName() + parts
}
