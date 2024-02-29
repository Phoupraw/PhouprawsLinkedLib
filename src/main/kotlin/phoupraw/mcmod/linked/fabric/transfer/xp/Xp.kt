@file:Suppress("UNUSED_ANONYMOUS_PARAMETER", "INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.fabric.transfer.xp

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import phoupraw.mcmod.linked.api.fabric.transfer.xp.MendingItemXpStorage
import phoupraw.mcmod.linked.api.fabric.transfer.xp.Xp
import phoupraw.mcmod.linked.fabric.transfer.GBlockApiLookup
import phoupraw.mcmod.linked.fabric.transfer.GEntityApiLookup
import phoupraw.mcmod.linked.fabric.transfer.base.SingletonVariant
import phoupraw.mcmod.linked.fabric.transfer.storage.ItemVariant
import phoupraw.mcmod.linked.impl.fabric.transfer.xp.FurnaceXpStorage
import phoupraw.mcmod.linked.lang.GenericApiLookup

@Deprecated("更新ITEM，移动到api", ReplaceWith("phoupraw.mcmod.linked.api.fabric.transfer.xp.Xp"))
object Xp : SingletonVariant() {
    @JvmField
    val SIDED = GBlockApiLookup<Storage<Xp>, Direction?>(Identifier("generic_api", "xp/sided"))
    @JvmField
    val ITEM = GenericApiLookup<Item, Storage<Xp>, ContainerItemContext>("xp/item")
    @JvmField
    val ENTITY = GEntityApiLookup<Storage<Xp>, Vec3d?>(Identifier("generic_api", "xp/entity"))

    init {
        SIDED.register(BlockEntityType.FURNACE, BlockEntityType.BLAST_FURNACE, BlockEntityType.SMOKER) { world, blockPos, blockState, blockEntity, context -> FurnaceXpStorage(blockEntity, world) }
        ITEM[Items.EXPERIENCE_BOTTLE].register(FullItemXpStorage(7, ItemVariant(Items.GLASS_BOTTLE)))
        ITEM[Items.GLASS_BOTTLE].register(EmptyItemXpStorage(7, ItemVariant(Items.EXPERIENCE_BOTTLE)))
        ITEM[Items.SCULK].register(FullItemXpStorage(1))
        ITEM.register(Items.SCULK_CATALYST, Items.SCULK_SENSOR, Items.CALIBRATED_SCULK_SENSOR, Items.SCULK_SHRIEKER, provider = FullItemXpStorage(5))
        ITEM.fallback.register { key, context ->
            if (EnchantmentHelper.getLevel(Enchantments.MENDING, context.itemVariant.toStack()) != 0) MendingItemXpStorage(context) else null
        }
    }
}
