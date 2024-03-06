package phoupraw.mcmod.linked.fabric.transfer.context

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.ScreenHandler
import net.minecraft.util.Hand
import phoupraw.mcmod.linked.fabric.transfer.item.InfCapacityItemStorage
import phoupraw.mcmod.linked.fabric.transfer.storage.*
import java.io.Closeable

open class RemainderContainerItemContext(
  val internalMainSlot: SingleSlotStorage<ItemVariant>,
  val overflow: (resource: ItemVariant, maxAmount: Long, transaction: TransactionContext) -> Unit,
  override val additionalSlots: List<SingleSlotStorage<ItemVariant>>
) : ContainerItemContext, Closeable {
    override val mainSlot: SingleSlotStorage<ItemVariant> = object : SingleSlotStorage<ItemVariant> by internalMainSlot {
        override fun insert(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long = internalMainSlot.insert(resource, maxAmount, transaction).let { it + receiver.insert(resource, maxAmount - it, transaction) }
    }
    val receiver: Storage<ItemVariant> = InfCapacityItemStorage()

    constructor(player: PlayerEntity, screenHandler: ScreenHandler) : this(SingleSlotStorage(screenHandler), player)
    constructor(player: PlayerEntity, hand: Hand) : this(PlayerInventoryStorage(player).getHandSlot(hand), player)
    constructor(mainSlot: SingleSlotStorage<ItemVariant>, player: PlayerEntity) : this(mainSlot, PlayerInventoryStorage(player))
    constructor(mainSlot: SingleSlotStorage<ItemVariant>, playerStorage: PlayerInventoryStorage) : this(mainSlot, playerStorage::offerOrDrop, playerStorage.slots)

    override fun insertOverflow(itemVariant: ItemVariant, maxAmount: Long, transactionContext: TransactionContext): Long {
        return receiver.insert(itemVariant, if (maxAmount == 0L) return 0 else maxAmount, transactionContext)
    }

    fun commit(transaction: TransactionContext? = null) {
        transaction.openThenCommit {
            Storages.move(receiver, CombinedStorage(listOf(
              internalMainSlot,
              InsertionOnlyStorage { resource, maxAmount, transaction -> maxAmount.apply { overflow(resource, maxAmount, transaction) } }
            )), transaction = it)
        }
    }

    override fun close() {
        check(receiver.empty) { "!receiver.empty; commit() has not been called; receiver=$receiver" }
    }
}