@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.fabric.transfer.item

import com.google.common.primitives.Ints
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import net.minecraft.item.ItemStack
import org.jetbrains.annotations.ApiStatus
import phoupraw.mcmod.linked.fabric.transfer.base.SimpleSingleSlotStorage
import phoupraw.mcmod.linked.fabric.transfer.storage.ItemVariant
import kotlin.math.min

@ApiStatus.Experimental
abstract class SingleItemStackStorage : SnapshotParticipant<ItemStack>(), SimpleSingleSlotStorage<ItemVariant> {
    protected abstract var stack: ItemStack
    protected open fun canInsert(resource: ItemVariant): Boolean = true
    protected open fun canExtract(resource: ItemVariant): Boolean = true
    protected open fun getCapacity(resource: ItemVariant): Int = resource.item.maxCount
    @get:JvmName("isResourceBlank")
    override val resourceBlank: Boolean get() = stack.isEmpty()
    override var resource: ItemVariant
        get() = ItemVariant(stack)
        set(value) {
            stack = value.toStack()
        }
    override var amount: Long
        get() = stack.count.toLong()
        set(value) {
            stack.count = Ints.saturatedCast(value)
        }
    override val capacity: Long get() = getCapacity(resource).toLong()
    override fun insert(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
        //        StoragePreconditions.notBlankNotNegative(resource, maxAmount)
        var currentStack = stack
        if ((resource.matches(currentStack) || currentStack.isEmpty) && canInsert(resource)) {
            val insertedAmount = min(maxAmount.toInt(), getCapacity(resource) - currentStack.count)
            if (insertedAmount > 0) {
                updateSnapshots(transaction)
                //                currentStack = stack
                if (currentStack.isEmpty) {
                    currentStack = resource.toStack(insertedAmount)
                } else {
                    currentStack.increment(insertedAmount)
                }
                stack = currentStack
                return insertedAmount.toLong()
            }
        }
        return 0
    }

    override fun extract(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
        //        StoragePreconditions.notBlankNotNegative(variant, maxAmount)
        val currentStack = stack
        if (resource.matches(currentStack) && canExtract(resource)) {
            val extracted = min(currentStack.count, maxAmount.toInt())
            if (extracted > 0) {
                updateSnapshots(transaction)
                //                currentStack = stack
                currentStack.decrement(extracted)
                //                stack = currentStack
                return extracted.toLong()
            }
        }
        return 0
    }

    override fun createSnapshot(): ItemStack = stack.copy()
    override fun readSnapshot(snapshot: ItemStack) {
        stack = snapshot
    }

    override fun toString(): String = "SingleItemStackStorage($stack)"
}
