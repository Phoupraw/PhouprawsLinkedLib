@file:Suppress("CAST_NEVER_SUCCEEDS")

package phoupraw.mcmod.linked.fabric.transfer.item

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SimpleInventory
import net.minecraft.nbt.NbtCompound
import phoupraw.mcmod.linked.mixin.minecraft.ASimpleInventory

class NbtSimpleInventory private constructor(val nbt: NbtCompound, size: Int) : SimpleInventory(size) {
    init {
        Inventories.readNbt(nbt, (this as ASimpleInventory).stacks)
        addListener { Inventories.writeNbt(nbt, (this as ASimpleInventory).stacks) }
    }

    companion object {
        val CACHE: Cache<NbtCompound, NbtSimpleInventory> = CacheBuilder.newBuilder().weakKeys().build()
        operator fun invoke(nbt: NbtCompound, size: Int): NbtSimpleInventory = CACHE.getIfPresent(nbt) ?: NbtSimpleInventory(nbt, size).also { CACHE.put(nbt, it) }
    }
}
