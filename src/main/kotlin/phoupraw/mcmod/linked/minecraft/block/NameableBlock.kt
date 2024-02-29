@file:Suppress("UNUSED_PARAMETER")

package phoupraw.mcmod.linked.minecraft.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import phoupraw.mcmod.linked.minecraft.block.entity.NameableBlockEntity

open class NameableBlock(settings: Settings) : Block(settings) {
    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)
        onPlaced(this, world, pos, state, placer, itemStack)
    }

    companion object {
        fun onPlaced(self: Block, world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
            if (itemStack.hasCustomName()) {
                (world.getBlockEntity(pos) as? NameableBlockEntity)?.apply {
                    if (!hasCustomName()) {
                        customName = itemStack.name
                    }
                }
            }
        }
    }
}
