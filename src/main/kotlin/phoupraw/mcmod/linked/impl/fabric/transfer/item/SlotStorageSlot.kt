package phoupraw.mcmod.linked.impl.fabric.transfer.item

import com.google.common.primitives.Ints
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import phoupraw.mcmod.linked.fabric.transfer.storage.ItemVariant
import phoupraw.mcmod.linked.fabric.transfer.storage.extract
import phoupraw.mcmod.linked.fabric.transfer.storage.insert

open class SlotStorageSlot(val storage: Storage<ItemVariant>, val view: StorageView<ItemVariant>, x: Int = 0, y: Int = 0, inventory: Inventory = SimpleInventory(0), index: Int = -1) : Slot(inventory, index, x, y) {
    override fun getStack(): ItemStack {
        return view.resource.toStack(Ints.saturatedCast(view.amount))
    }

    override fun setStackNoCallbacks(stack: ItemStack) {
        storage.insert(null, ItemVariant(stack), stack.count.toLong())
    }

    override fun markDirty() {
    }

    override fun getMaxItemCount(): Int {
        return Ints.saturatedCast(view.capacity)
    }

    override fun getMaxItemCount(stack: ItemStack): Int {
        return Transaction.openOuter().use {
            view.extract(it)
            storage.insert(ItemVariant(stack), stack.count.toLong(), it).toInt()
        }
    }

    override fun takeStack(amount: Int): ItemStack {
        return view.resource.toStack(view.extract(maxAmount = amount.toLong()).toInt())
    }

    override fun canInsert(stack: ItemStack): Boolean {
        return getMaxItemCount(stack) > 0
    }
}