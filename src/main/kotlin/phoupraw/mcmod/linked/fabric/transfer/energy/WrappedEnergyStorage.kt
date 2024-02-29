@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.fabric.transfer.energy

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import org.jetbrains.annotations.Contract
import phoupraw.mcmod.linked.fabric.transfer.energy.Energy.EU
import phoupraw.mcmod.linked.fabric.transfer.storage.ContainerItemContext
import phoupraw.mcmod.linked.fabric.transfer.storage.InventoryStorage
import phoupraw.mcmod.linked.fabric.transfer.storage.Storage
import team.reborn.energy.api.EnergyStorage

/**
 * 把[EnergyStorage]包装成`[Storage]<[Energy]>`
 * @see EnergyStorageWrapper
 */
class WrappedEnergyStorage private constructor(var back: EnergyStorage) : SingleSlotStorage<Energy> {
    override fun supportsInsertion(): Boolean {
        return back.supportsInsertion()
    }

    override fun insert(resource: Energy, maxAmount: Long, transaction: TransactionContext): Long {
        if (maxAmount < EU) return 0
        return back.insert(maxAmount / EU, transaction) * EU
    }

    override fun supportsExtraction(): Boolean {
        return back.supportsExtraction()
    }

    override fun extract(resource: Energy, maxAmount: Long, transaction: TransactionContext): Long {
        if (maxAmount < EU) return 0
        return back.extract(maxAmount / EU, transaction) * EU
    }

    override val resourceBlank: Boolean @JvmName("isResourceBlank") get() = amount == 0L
    override val resource: Energy get() = Energy
    override val amount: Long get() = back.amount * EU
    override val capacity: Long get() = back.capacity * EU

    companion object {
        fun constOf(stack: ItemStack): Storage<Energy> {
            return of(ContainerItemContext(stack))
        }

        fun constOf(itemVariant: ItemVariant): Storage<Energy> {
            return of(ContainerItemContext(itemVariant, 1))
        }

        fun of(stack: ItemStack): Storage<Energy> {
            return of(ContainerItemContext(InventoryStorage(SimpleInventory(stack)).getSlot(0)))
        }
        @Contract("null -> null; !null -> !null")
        fun ofNullable(s: EnergyStorage?): Storage<Energy>? {
            return if (s == null) null else invoke(s)
        }
        @JvmStatic
        operator fun invoke(s: EnergyStorage): Storage<Energy> {
            return (s as? EnergyStorageWrapper)?.back ?: WrappedEnergyStorage(s)
        }

        fun nullableOf(context: ContainerItemContext): Storage<Energy>? {
            return ofNullable(context.find<EnergyStorage>(EnergyStorage.ITEM))
        }

        fun of(context: ContainerItemContext): Storage<Energy> {
            return nullableOf(context) ?: Storage()
        }
    }
}
