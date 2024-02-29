@file:Suppress("UNUSED_ANONYMOUS_PARAMETER", "INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.api.fabric.transfer.xp

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.math.Direction
import phoupraw.mcmod.linked.api.minecraft.util.BlockApiLookup
import phoupraw.mcmod.linked.api.util.SingleApiLookup
import phoupraw.mcmod.linked.fabric.transfer.base.SingletonVariant
import phoupraw.mcmod.linked.fabric.transfer.storage.ItemVariant
import phoupraw.mcmod.linked.impl.fabric.transfer.xp.FurnaceXpStorage

object Xp : SingletonVariant() {
    @JvmField
    val SIDED = BlockApiLookup<Direction?, Storage<Xp>>("xp/sided")
    @JvmField
    val ITEM = SingleApiLookup<Item, ContainerItemContext, Storage<Xp>>("xp/item")
    //@JvmField
    //val ENTITY = GEntityApiLookup<Storage<Xp>, Vec3d?>(Identifier("generic_api", "xp/entity"))
    init {
        SIDED.register(BlockEntityType.FURNACE, BlockEntityType.BLAST_FURNACE, BlockEntityType.SMOKER) { world, blockPos, blockState, blockEntity, context -> FurnaceXpStorage(blockEntity, world) }
        ITEM[Items.EXPERIENCE_BOTTLE].register(FullItemXpStorage.finderOf(7, ItemVariant(Items.GLASS_BOTTLE)))
        ITEM[Items.GLASS_BOTTLE].register(EmptyItemXpStorage.finderOf(7, ItemVariant(Items.EXPERIENCE_BOTTLE)))
        ITEM[Items.SCULK].register(FullItemXpStorage.finderOf(1))
        ITEM.register(Items.SCULK_CATALYST, Items.SCULK_SENSOR, Items.CALIBRATED_SCULK_SENSOR, Items.SCULK_SHRIEKER, provider = FullItemXpStorage.finderOf(5))
        ITEM.fallback.register { key, context ->
            if (EnchantmentHelper.getLevel(Enchantments.MENDING, context.itemVariant.toStack()) != 0) MendingItemXpStorage(context) else null
        }
    }
}
