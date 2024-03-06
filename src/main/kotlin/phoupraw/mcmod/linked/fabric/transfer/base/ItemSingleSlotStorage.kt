@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.fabric.transfer.base

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.item.Item
import org.jetbrains.annotations.ApiStatus
import kotlin.math.min

@ApiStatus.Experimental
abstract class ItemSingleSlotStorage<T : TransferVariant<*>>(open val context: ContainerItemContext) : SingleSlotStorage<T> {
    val item: Item = context.itemVariant.item
    protected abstract val blankResource: T
    protected abstract fun getResource(currentVariant: ItemVariant): T
    protected abstract fun getAmount(currentVariant: ItemVariant): Long
    protected abstract fun getCapacity(currentVariant: ItemVariant, variant: T): Long
    protected abstract fun getUpdatedVariant(currentVariant: ItemVariant, newResource: T, newAmount: Long): ItemVariant
    protected open fun canInsert(resource: T): Boolean = true
    protected open fun canExtract(resource: T): Boolean = true
    private fun tryUpdateStorage(newResource: T, newAmount: Long, tx: TransactionContext): Boolean {
        val originalVariant: ItemVariant = context.itemVariant
        val updatedVariant = getUpdatedVariant(originalVariant, newResource, newAmount)
        return (if (updatedVariant.blank) context.extract(originalVariant, 1, tx) else context.exchange(updatedVariant, 1, tx)) == 1L
    }

    override fun supportsInsertion(): Boolean = context.itemVariant.isOf(item)
    override fun insert(resource: T, maxAmount: Long, transaction: TransactionContext): Long {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount)
        // Check insertion.
        if (!canInsert(resource)) return 0
        // Check item.
        val currentVariant: ItemVariant = context.itemVariant
        if (!currentVariant.isOf(item)) return 0
        val currentAmount = getAmount(currentVariant)
        val currentResource = getResource(currentVariant)
        //long inserted = 0;
        //while (true) {
        val amount: Long = if (currentResource.blank || currentAmount == 0L) {
            // Insertion into empty storage.
            min(getCapacity(currentVariant, resource), maxAmount)
        } else if (currentResource == resource) {
            // Insertion into storage with an existing resource.
            min(getCapacity(currentVariant, resource) - currentAmount, maxAmount)
        } else {
            0
        }
        if (amount > 0) {
            if (tryUpdateStorage(resource, currentAmount + amount, transaction)) {
                return amount
            }
        }
        //}
        return 0
    }

    override fun supportsExtraction(): Boolean = context.itemVariant.isOf(item)
    override fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount)
        // Check extraction.
        if (!canExtract(resource)) return 0
        // Check item.
        if (!context.itemVariant.isOf(item)) return 0
        val amount = getAmount(context.itemVariant)
        val currentResource = getResource(context.itemVariant)
        val extracted = if (resource == currentResource) min(maxAmount, amount) else 0
        if (extracted > 0 && tryUpdateStorage(currentResource, amount - extracted, transaction)) {
            return extracted
        }
        return 0
    }
    @get:JvmName("isResourceBlank")
    override val resourceBlank: Boolean get() = resource.blank
    override val resource: T get() = if (context.itemVariant.isOf(item)) getResource(context.itemVariant) else blankResource
    override val amount: Long get() = if (context.itemVariant.isOf(item)) getAmount(context.itemVariant) else 0
    override val capacity: Long get() = context.itemVariant.run { if (isOf(item)) getCapacity(this, resource) else 0 }
    override fun toString(): String = "ItemSingleVariantStorage[$context/$item]"
}
