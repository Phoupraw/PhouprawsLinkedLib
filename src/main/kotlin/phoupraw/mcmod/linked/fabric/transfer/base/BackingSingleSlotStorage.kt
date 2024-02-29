package phoupraw.mcmod.linked.fabric.transfer.base

import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class BackingSingleSlotStorage<T, out S : SingleSlotStorage<T>>(back: S) : BackingSlottedStorage<T, S>(back), SingleSlotStorage<T> by back {
    override val slotCount: Int get() = back.slotCount
    override fun getSlot(slot: Int): SingleSlotStorage<T> = back.getSlot(slot)
    override fun iterator(): Iterator<SingleSlotStorage<T>> = back.iterator()
    //    override fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long {
    //        return back.extract(resource, maxAmount, transaction)
    //    }
    //
    //    override fun insert(resource: T, maxAmount: Long, transaction: TransactionContext): Long {
    //        return back.insert(resource, maxAmount, transaction)
    //    }
    //
    //    override fun nonEmptyIterator(): Iterator<StorageView<T>> {
    //        return back.nonEmptyIterator()
    //    }
    //
    //    override fun nonEmptyViews(): Iterable<StorageView<T>> {
    //        return back.nonEmptyViews()
    //    }
    //
    //    override fun supportsExtraction(): Boolean {
    //        return back.supportsExtraction()
    //    }
    //
    //    override fun supportsInsertion(): Boolean {
    //        return back.supportsInsertion()
    //    }
    //
    //    override val version: Long
    //        get() = back.version
}