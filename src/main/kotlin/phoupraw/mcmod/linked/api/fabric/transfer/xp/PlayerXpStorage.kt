@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.api.fabric.transfer.xp

import com.google.common.primitives.Ints
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import net.minecraft.entity.player.PlayerEntity
import org.jetbrains.annotations.ApiStatus
import kotlin.math.ceil
import kotlin.math.min

@ApiStatus.Experimental
open class PlayerXpStorage(val player: PlayerEntity) : SnapshotParticipant<XpLevel>(), SingleSlotStorage<Xp> {
    @get:JvmName("isResourceBlank")
    override val resourceBlank: Boolean get() = amount == 0L
    override val resource: Xp get() = Xp
    override val amount: Long get() = player.xpValue.floor
    override val capacity: Long get() = XpLevel(Int.MAX_VALUE.toDouble()).floor
    override fun createSnapshot(): XpLevel = player.xpLevel
    override fun readSnapshot(snapshot: XpLevel) {
        player.xpLevel = snapshot
    }

    override fun insert(resource: Xp, maxAmount: Long, transaction: TransactionContext): Long = insert(maxAmount, transaction)
    override fun extract(resource: Xp, maxAmount: Long, transaction: TransactionContext): Long = extract(maxAmount, transaction)
    fun insert(maxAmount: Long, transaction: TransactionContext): Long {
        updateSnapshots(transaction)
        val amount = min(maxAmount, capacity - amount)
        var toInsert = amount
        while (toInsert > 0) {
            Ints.saturatedCast(toInsert).also {
                toInsert -= it
                player.addExperience(it)
            }
        }
        return amount
    }

    fun extract(maxAmount: Long, transaction: TransactionContext): Long {
        updateSnapshots(transaction)
        val amount = min(maxAmount, amount)
        var toExtract = amount
        while (toExtract > 0) {
            Ints.saturatedCast(-toExtract).also {
                toExtract += it
                player.addExperience(it)
            }
        }
        return amount
    }

    fun insertUntilLevelUp(transaction: TransactionContext): Long = insertUntilLevelUp(Long.MAX_VALUE, transaction)
    fun insertUntilLevelUp(maxAmount: Long, transaction: TransactionContext): Long = insert(min(maxAmount, ceil(player.xpLevel.run { next - this }.value.v).toLong()), transaction)
    fun extractUntilZeroOrLevelDown(transaction: TransactionContext) = extractUntilZeroOrLevelDown(Long.MAX_VALUE, transaction)
    fun extractUntilZeroOrLevelDown(maxAmount: Long, transaction: TransactionContext): Long {
        val l = player.xpLevel
        val floor = extract(min(maxAmount, (l - l.present).floor), transaction)
        if (floor <= 0) {
            return extract(min(maxAmount, (l - l.last).floor), transaction)
            //        val amount = extract(min(maxAmount, (l - l.present).ceil), transaction)
            //        if (amount > 0) {
            //            return amount
            //        }
            //        return extract(min(maxAmount - amount, (l - l.last).floor), transaction)
        }
        if (maxAmount > floor) {
            transaction.openNested().use {
                extract(1, it)
                if (player.xpLevel >= l.present) {
                    it.commit()
                    return floor + 1
                }
            }
        }
        return floor
    }
    //    fun asLevelStepLimited(): SingleSlotStorage<Xp> = object : PlayerXpStorage(player),SingleSlotStorage<Xp> {
    //        override fun insert(resource: Xp, maxAmount: Long, transaction: TransactionContext): Long {
    //            return super.insert(resource,  min(maxAmount, ceil(player.xpLevel.restToNextLevel.toValue().v).toLong()), transaction)
    //        }
    //    }
    //    class Normal(player: PlayerEntity) : PlayerXpStorage(player) {
    //        override fun insert(resource: Xp, maxAmount: Long, transaction: TransactionContext): Long {
    //            return insert( maxAmount, transaction)
    //        }
    //
    //        override fun extract(resource: Xp, maxAmount: Long, transaction: TransactionContext): Long {
    //            return extract(maxAmount, transaction)
    //        }
    //    }
    //    companion object {
    //        private inline fun piecewise(v: Double, t1: Double, t2: Double, calc: (a: Double, b: Double, c: Double) -> Double): Double {
    //            val a: Double
    //            val b: Double
    //            val c: Double
    //            if (v < t1) {
    //                a = 1.0
    //                b = 6.0
    //                c = 0.0
    //            } else if (v < t2) {
    //                a = 2.5
    //                b = -40.5
    //                c = 360.0
    //            } else {
    //                a = 4.5
    //                b = -162.5
    //                c = 2220.0
    //            }
    //            return calc(a, b, c)
    //        }
    //
    //        fun toValue(level: Double) = piecewise(level, 16.0, 31.0) { a, b, c -> a * level * level + b * level + c }
    //        fun toLevel(value: Double) = piecewise(value, 352.0, 1507.0) { a, b, c -> (-b + sqrt(b * b - 4 * a * (c - value))) / (2 * a) }
    //    }
}
