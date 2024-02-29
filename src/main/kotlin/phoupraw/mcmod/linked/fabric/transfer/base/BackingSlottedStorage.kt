package phoupraw.mcmod.linked.fabric.transfer.base

import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class BackingSlottedStorage<T, out S : SlottedStorage<T>>(back: S) : BackingStorage<T, S>(back), SlottedStorage<T> by back {
    override fun iterator(): Iterator<SingleSlotStorage<T>> = back.iterator()
    override fun nonEmptyIterator(): Iterator<SingleSlotStorage<T>> = super<SlottedStorage>.nonEmptyIterator()
    override fun nonEmptyViews(): Iterable<SingleSlotStorage<T>> = super<SlottedStorage>.nonEmptyViews()
}