package phoupraw.mcmod.linked.fabric.transfer.energy

import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import phoupraw.mcmod.linked.fabric.transfer.energy.Energy.EU
import phoupraw.mcmod.linked.fabric.transfer.storage.amount
import phoupraw.mcmod.linked.fabric.transfer.storage.capacity
import team.reborn.energy.api.EnergyStorage

open class EnergyStorageWrapper private constructor(val back: Storage<Energy>) : EnergyStorage {
    override fun supportsInsertion(): Boolean {
        return back.supportsInsertion()
    }

    override fun insert(maxAmount: Long, transaction: TransactionContext): Long {
        if (maxAmount == 0L) return 0
        var thatMaxAmount: Long = maxAmount * EU
        while (true) {
            transaction.openNested().use { t ->
                val amount: Long = back.insert(Energy, thatMaxAmount, t)
                if (amount % EU == 0L) {
                    t.commit()
                    return amount / EU
                }
                thatMaxAmount = amount
            }
        }
    }

    override fun supportsExtraction(): Boolean {
        return back.supportsExtraction()
    }

    override fun extract(maxAmount: Long, transaction: TransactionContext): Long {
        if (maxAmount == 0L) return 0
        var thatMaxAmount: Long = maxAmount * EU
        while (true) {
            transaction.openNested().use { t ->
                val amount: Long = back.extract(Energy, thatMaxAmount, t)
                if (amount % EU == 0L) {
                    t.commit()
                    return amount / EU
                }
                thatMaxAmount = amount
            }
        }
    }

    override fun getAmount(): Long = back.amount / EU
    override fun getCapacity(): Long = back.capacity / EU

    companion object {
        operator fun invoke(back: Storage<Energy>): EnergyStorage {
            return (back as? WrappedEnergyStorage)?.back ?: EnergyStorageWrapper(back)
        }
    }
}
