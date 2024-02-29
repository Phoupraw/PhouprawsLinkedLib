@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.fabric.transfer.base

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import org.jetbrains.annotations.ApiStatus
import kotlin.math.min

//typealias ResourceAmount<T> = Pair<T,Long>
@ApiStatus.Experimental
abstract class DirectSingleSlotStorage<T : TransferVariant<*>> : SnapshotParticipant<ResourceAmount<T>>(), SimpleSingleSlotStorage<T> {
    @Suppress("LeakingThis")
    override var resource: T = blankVariant
    override var amount: Long = 0
    //    constructor(resource: T, amount: Long) : this() {
    //        this.resource = resource
    //        this.amount = amount
    //    }
    protected abstract val blankVariant: T
    protected abstract fun getCapacity(resource: T): Long
    protected open fun canInsert(resource: T): Boolean = true
    protected open fun canExtract(resource: T): Boolean = true
    //    open fun writeNbt(root: NbtCompound): NbtCompound = (this as StorageView<T>).writeNbt(root)//root.write(resource, amount)
    override fun insert(resource: T, maxAmount: Long, transaction: TransactionContext): Long {
        //        StoragePreconditions.notBlankNotNegative(resource, maxAmount)
        if ((resource == this.resource || this.resource.blank) && canInsert(resource)) {
            val insertedAmount = min(maxAmount, getCapacity(resource) - amount)
            if (insertedAmount > 0) {
                updateSnapshots(transaction)
                if (this.resource.blank) {
                    this.resource = resource
                    amount = insertedAmount
                } else {
                    amount += insertedAmount
                }
                return insertedAmount
            }
        }
        return 0
    }

    override fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long {
        //        StoragePreconditions.notBlankNotNegative(resource, maxAmount)
        if (resource == this.resource && canExtract(resource)) {
            val extractedAmount = min(maxAmount, amount)
            if (extractedAmount > 0) {
                updateSnapshots(transaction)
                amount -= extractedAmount
                if (amount == 0L) {
                    this.resource = blankVariant
                }
                return extractedAmount
            }
        }
        return 0
    }
    @get:JvmName("isResourceBlank")
    override val resourceBlank: Boolean
        get() = resource.blank
    override val capacity: Long
        get() = getCapacity(resource)

    override fun createSnapshot(): ResourceAmount<T> = ResourceAmount(resource, amount)
    override fun readSnapshot(snapshot: ResourceAmount<T>) {
        resource = snapshot.resource
        amount = snapshot.amount
    }

    override fun toString(): String = "SingleSlotStorage($resource, $amount)"
}

