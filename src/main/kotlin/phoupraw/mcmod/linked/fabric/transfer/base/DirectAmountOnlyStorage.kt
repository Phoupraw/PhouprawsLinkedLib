@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.fabric.transfer.base

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import kotlin.math.min

abstract class DirectAmountOnlyStorage<T>(override var amount: Long = 0) : SnapshotParticipant<Long>(), SimpleSingleSlotStorage<T> {
    override fun createSnapshot(): Long {
        return amount
    }

    override fun readSnapshot(snapshot: Long) {
        amount = snapshot
    }

    override val resourceBlank: Boolean @JvmName("isResourceBlank") get() = amount != 0L
    override fun insert(resource: T, maxAmount: Long, transaction: TransactionContext): Long {
        val amount: Long = amount
        val add = min(maxAmount, capacity - amount)
        if (add != 0L) {
            updateSnapshots(transaction)
            this.amount = amount + add
        }
        return add
    }

    override fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long {
        val amount: Long = amount
        val sub = min(maxAmount.toDouble(), amount.toDouble()).toLong()
        if (sub != 0L) {
            updateSnapshots(transaction)
            this.amount = amount - sub
        }
        return sub
    }
}
