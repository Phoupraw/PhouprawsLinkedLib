package phoupraw.mcmod.linked.impl.fabric.transfer.xp

import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import net.minecraft.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.util.Identifier
import net.minecraft.world.World
import org.apache.commons.lang3.tuple.MutablePair
import org.jetbrains.annotations.ApiStatus
import phoupraw.mcmod.linked.api.fabric.transfer.xp.Xp
import phoupraw.mcmod.linked.api.minecraft.misc.Math2
import java.util.*
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToLong

//TODO 跨版本：1.20.1-1.20.2
@ApiStatus.Experimental
class FurnaceXpStorage : SnapshotParticipant<Boolean>, SingleSlotStorage<Xp>, ExtractionOnlyStorage<Xp> {
    val furnace: AbstractFurnaceBlockEntity
    val world: World
    val recipeStats: NavigableMap<Float, MutablePair<Identifier, Int>>
    override var amount: Long = -1
        get() {
            if (field == -1L) {
                var amount = 0.0
                for ((key, value) in recipeStats) {
                    amount += key * value.getRight()
                }
                field = amount.roundToLong()
            }
            return field
        }
    /**
     * SB Idea
     */
    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(furnace: AbstractFurnaceBlockEntity, world: World = furnace.world!!) : super() {
        this.furnace = furnace
        this.world = world
        this.recipeStats = TreeMap(Comparator.reverseOrder())
        //        val world = furnace.world
        //        if (world == null) {
        //            Trifle.LOGGER.catching(NullPointerException("furnace.world; furnace=$furnace"))
        //            return
        //        }
        //        for (entry in (furnace ).recipesUsed.object2IntEntrySet()) {
        //            val id = entry.key
        //            (world.recipeManager[id].getOrNull() as? AbstractCookingRecipe)?.apply {
        //                recipeStats[experience] = MutablePair.of(id, entry.intValue)
        //            }
        //        }
    }

    override fun extract(resource: Xp, maxAmount: Long, transaction: TransactionContext): Long {
        //if (maxAmount < getAmount()) return 0;
        updateSnapshots(transaction)
        var amount = 0.0
        for ((exp, pair) in recipeStats) {
            val count = pair.getRight()
            val extractedCount = min(count, floor((maxAmount - amount) / exp).toInt())
            pair.setRight(count - extractedCount)
            amount += exp * extractedCount
        }
        this.amount = -1
        return Math2.twoPoint(amount, world.random).roundToLong()
        //        return (furnace.world?.run { twoPoint(amount, random) } ?: twoPoint(amount)).roundToLong()
    }

    override val capacity: Long
        get() = amount

    override fun createSnapshot(): Boolean {
        return amount == 0L
    }

    override fun readSnapshot(snapshot: Boolean) {
        if (snapshot) {
            amount = 0
        }
    }

    override fun onFinalCommit() {
        super.onFinalCommit()
        //        val recipesUsed = this.furnace.recipesUsed
        //        recipeStats.values.removeIf { (id, count) ->
        //            (count == 0).also {
        //                if (it) {
        //                    recipesUsed.removeInt(id)
        //                } else {
        //                    recipesUsed.put(id, count)
        //                }
        //            }
        //        }
        this.furnace.markDirty()
    }

    override val resourceBlank: Boolean get() = amount == 0L
    override val resource: Xp get() = Xp
}