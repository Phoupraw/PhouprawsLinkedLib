package phoupraw.mcmod.linked.minecraft.block.entity

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * @see BlockEntityBlock
 */
interface TickableBlockEntity {
    fun tick(world: World, blockPos: BlockPos, blockState: BlockState)

    companion object {
        /**
         * 如果把`<T extends BlockEntity & TickableBlockEntity>`改为`<T extends TickableBlockEntity>`，编译不报错，但运行时崩溃
         * @see BlockEntityTicker
         */
        fun <T> tick(world: World, blockPos: BlockPos, blockState: BlockState, blockEntity: T) where T : BlockEntity, T : TickableBlockEntity {
            blockEntity.tick(world, blockPos, blockState)
        }
    }
}
