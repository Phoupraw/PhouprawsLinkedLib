package phoupraw.mcmod.linked.fabric.transfer.storage

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.impl.transfer.TransferApiImpl
import net.fabricmc.fabric.impl.transfer.context.ConstantContainerItemContext
import net.fabricmc.fabric.impl.transfer.context.PlayerContainerItemContext
import net.fabricmc.fabric.impl.transfer.context.SingleSlotContainerItemContext
import net.fabricmc.fabric.impl.transfer.item.CursorSlotWrapper
import net.fabricmc.fabric.impl.transfer.item.InventoryStorageImpl
import net.fabricmc.fabric.impl.transfer.item.ItemVariantImpl
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.math.Direction
import org.jetbrains.annotations.Range
import phoupraw.mcmod.linked.api.fabric.transfer.xp.MendingItemXpStorage
import phoupraw.mcmod.linked.api.fabric.transfer.xp.Xp
import phoupraw.mcmod.linked.fabric.transfer.base.CombinedSingleSlotStorage
import phoupraw.mcmod.linked.fabric.transfer.base.SimpleSingleSlotStorage
import phoupraw.mcmod.linked.fabric.transfer.item.DirectSingleItemStackStorage
import phoupraw.mcmod.linked.fabric.transfer.item.EquipmentSlotItemStorage
import phoupraw.mcmod.linked.fabric.transfer.item.InfCapacitySingleItemStorage
import phoupraw.mcmod.linked.lang.DefaultedMap
import phoupraw.mcmod.linked.minecraft.FixedNbtCompound
import phoupraw.mcmod.linked.minecraft.getLongOrNull
import phoupraw.mcmod.linked.minecraft.getOrDefault
import phoupraw.mcmod.linked.minecraft.putVarInt

object Storages {
    @JvmStatic
    @JvmOverloads
    fun <T> move(from: Storage<in T>?, to: Storage<in T>?, resource: T, maxAmount: @Range(from = 0, to = Long.MAX_VALUE) Long = Long.MAX_VALUE, transaction: TransactionContext? = null): Long {
        from ?: return 0
        to ?: return 0
        transaction.openNested().use {
            val amount = to.insert(resource, from.simulateExtract(it, resource, maxAmount), it)
            if (from.extract(resource, amount, it) == amount) {
                it.commit()
                return amount
            }
        }
        return 0
    }
    @JvmStatic
    @JvmOverloads
    fun <T> move(from: Storage<T>?, to: Storage<in T>?, test: (T) -> Boolean = { true }, maxAmount: @Range(from = 0, to = Long.MAX_VALUE) Long = Long.MAX_VALUE, transaction: TransactionContext? = null): Map<T, Long> {
        if (from == null || to == null) return emptyMap()
        val moved = DefaultedMap(LinkedHashMap<T, Long>(), 0)
        var movedAmount = 0L
        for (view: StorageView<T> in from.nonEmptyIterator()) {
            if (view.resourceBlank) continue
            val resource = view.resource
            if (!test(resource)) continue
            transaction.openNested().use {
                val amount = to.insert(resource, view.simulateExtract(it, resource, maxAmount - movedAmount), it)
                if (from.extract(resource, amount, it) == amount) {
                    moved[resource] += amount
                    movedAmount += amount
                    it.commit()
                }
            }
            if (movedAmount == maxAmount) break
        }
        return moved.back
    }
}

operator fun <T> ResourceAmount<T>.component1(): T = resource
operator fun <T> ResourceAmount<T>.component2(): Long = amount
operator fun <T> StorageView<T>.component1(): T = resource
operator fun <T> StorageView<T>.component2(): Long = amount
@JvmOverloads
fun StorageView<out TransferVariant<*>>.writeNbt(nbtView: NbtCompound = NbtCompound()) = nbtView.apply {
    if (resourceBlank) return@apply
    resource.toNbt().also {
        if (it !== FixedNbtCompound.Empty) {
            put("resource", it)
        }
    }
    if (amount != 1L) {
        putVarInt("amount", amount)
    }
}
@JvmOverloads
fun SingleSlotStorage<out TransferVariant<*>>.writeNbt(nbtView: NbtCompound = NbtCompound()) = (this as StorageView<out TransferVariant<*>>).writeNbt(nbtView)
@JvmOverloads
fun Iterable<StorageView<out TransferVariant<*>>>.writeNbt(nbtViews: NbtList = NbtList()) = nbtViews.apply {
    forEachIndexed { i, view: StorageView<out TransferVariant<*>> ->
        if (view.resourceBlank) return@forEachIndexed
        add(view.writeNbt().apply {
            putVarInt("slot", i)
        })
    }
}

fun SimpleSingleSlotStorage<ItemVariant>.readNbt(nbtView: NbtCompound) {
    resource = ItemVariant(nbtView.getCompound("resource"))
    if (!resource.blank) {
        amount = nbtView.getOrDefault("amount", 1L)
    }
}

fun List<SimpleSingleSlotStorage<ItemVariant>>.readNbt(nbtViews: NbtList) {
    for (it in this) {
        it.resource = ItemVariant()
    }
    for (nbtView in nbtViews) {
        if (nbtView !is NbtCompound) continue
        val i = nbtView.getInt("slot")
        if (i !in indices) continue
        this[i].readNbt(nbtView)
    }
}

fun ItemResourceAmount(nbt: NbtCompound): ResourceAmount<ItemVariant> = ResourceAmount(
  nbt.getCompound("resource")?.let { ItemVariant(it) } ?: ItemVariant(),
  nbt.getLongOrNull("amount") ?: 1
)

fun <T : TransferVariant<*>> NbtCompound(resource: T, amount: Long): NbtCompound = NbtCompound().apply {
    if (!resource.blank) {
        put("resource", resource.toNbt())
        if (amount != 1L) {
            putLong("amount", amount)
        }
    }
}
//fun NbtCompound.write(resource: TransferVariant<*>, amount: Long = 1): NbtCompound {
//    if (!resource.blank) {
//        put("variant", resource.toNbt())
//        if (amount != 1L) {
//            putLong("amount", amount)
//        }
//    }
//    return this
//}
@get:JvmName("isEmpty")
val StorageView<*>.empty: Boolean get() = resourceBlank || amount == 0L
@get:JvmName("isEmpty")
val Storage<*>.empty: Boolean get() = all { it.empty }
@get:JvmName("isEmpty")
val SingleSlotStorage<*>.empty: Boolean get() = (this as StorageView<*>).empty
@get:JvmName("isEmpty")
val ContainerItemContext.empty: Boolean get() = itemVariant.blank || amount == 0L
@get:JvmName("isFull")
val StorageView<*>.full: Boolean get() = amount == capacity
@get:JvmName("isFull")
val Storage<*>.full: Boolean get() = all { it.full }
@get:JvmName("isFull")
val SingleSlotStorage<*>.full: Boolean get() = (this as StorageView<*>).full
val Iterable<StorageView<*>>.amount get() = sumOf { it.amount }
val Iterable<StorageView<*>>.capacity get() = sumOf { it.capacity }
@JvmOverloads
fun ItemVariant(item: Item = Items.AIR, nbt: NbtCompound? = null): ItemVariant = ItemVariantImpl.of(item, nbt)
//@JvmOverloads
//fun ItemVariant(item: Item = Items.AIR): ItemVariant = ItemVariant(item,null)
fun ItemVariant(itemStack: ItemStack): ItemVariant = ItemVariant(itemStack.item, itemStack.nbt)
fun ItemVariant(nbt: NbtCompound): ItemVariant = ItemVariantImpl.fromNbt(nbt)
//fun ItemVariant.with(nbt: NbtCompound?) = ItemVariant(item, nbt)
fun LivingEntityItemStorage(entity: LivingEntity): SlottedStorage<ItemVariant> = CombinedSingleSlotStorage(EquipmentSlot.entries.map { EquipmentSlotItemStorage(entity, it) })
fun MendingEntityXpStorage(entity: LivingEntity): SlottedStorage<Xp> = CombinedSingleSlotStorage(EquipmentSlot.entries.mapNotNull { if (EnchantmentHelper.getLevel(Enchantments.MENDING, entity.getEquippedStack(it)) != 0) MendingItemXpStorage(ContainerItemContext(EquipmentSlotItemStorage(entity, it))) else null })
fun TransactionContext?.openNested(): Transaction = Transaction.openNested(this)
inline fun <T> TransactionContext?.openThenCommit(transferBy: (t: Transaction) -> T): T = openNested().use { transferBy(it).apply { it.commit() } }
fun <T> Storage<T>.insert(transaction: TransactionContext? = null, resource: T, maxAmount: Long = Long.MAX_VALUE): Long = transaction.openThenCommit { insert(resource, maxAmount, it) }
fun <T> StorageView<T>.extract(transaction: TransactionContext? = null, resource: T = this.resource, maxAmount: Long = this.amount): Long = transaction.openThenCommit { extract(resource, maxAmount, it) }
fun <T> Storage<T>.simulateInsert(transaction: TransactionContext? = null, resource: T, maxAmount: Long = Long.MAX_VALUE): Long = transaction.openNested().use { insert(resource, maxAmount, it) }
fun <T> Storage<T>.simulateExtract(transaction: TransactionContext? = null, resource: T, maxAmount: Long = Long.MAX_VALUE): Long = transaction.openNested().use { extract(resource, maxAmount, it) }
fun <T> StorageView<T>.simulateExtract(transaction: TransactionContext? = null, resource: T = this.resource, maxAmount: Long = this.amount): Long = transaction.openNested().use { extract(resource, maxAmount, it) }
@Suppress("UNCHECKED_CAST")
fun <T> Storage(): Storage<T> = TransferApiImpl.EMPTY_STORAGE as Storage<T>
fun SingleSlotStorage(itemStack: ItemStack): SingleSlotStorage<ItemVariant> = DirectSingleItemStackStorage(itemStack)
fun SingleSlotStorage(resource: ItemVariant = ItemVariant(), amount: Long = 1): SingleSlotStorage<ItemVariant> = InfCapacitySingleItemStorage(resource, amount)
fun SingleSlotStorage(screenHandler: ScreenHandler): SingleSlotStorage<ItemVariant> = CursorSlotWrapper.get(screenHandler)
@JvmOverloads
fun InventoryStorage(inventory: Inventory, side: Direction? = null): InventoryStorage = InventoryStorageImpl.of(inventory, side)
fun PlayerInventoryStorage(player: PlayerEntity): PlayerInventoryStorage = PlayerInventoryStorage(player.inventory)
fun PlayerInventoryStorage(inventory: PlayerInventory): PlayerInventoryStorage = InventoryStorage(inventory) as PlayerInventoryStorage
fun ContainerItemContext(slot: SingleSlotStorage<ItemVariant>): ContainerItemContext = SingleSlotContainerItemContext(slot)
fun ContainerItemContext(itemStack: ItemStack): ContainerItemContext = ContainerItemContext(ItemVariant(itemStack), itemStack.count.toLong())
fun ContainerItemContext(resource: ItemVariant, amount: Long): ContainerItemContext = ConstantContainerItemContext(resource, amount)
fun ContainerItemContext(player: PlayerEntity, hand: Hand): ContainerItemContext = PlayerContainerItemContext(player, hand)
val FluidVariant.name: Text get() = FluidVariantAttributes.getName(this)
val FluidVariant.luminance get() = FluidVariantAttributes.getLuminance(this)
val FluidVariant.temperature get() = FluidVariantAttributes.getTemperature(this)
val FluidVariant.lighterThanAir @JvmName("isLighterThanAir") get() = FluidVariantAttributes.isLighterThanAir(this)
//FIXME 勾八编译器报什么鬼错
//fun <T> Event<T>.registerTrifle(listener: T) = this.register(TIdentifiers.DEFAULT, listener)
