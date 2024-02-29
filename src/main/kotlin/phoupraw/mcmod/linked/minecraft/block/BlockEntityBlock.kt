@file:Suppress("OVERRIDE_DEPRECATION", "DEPRECATION", "UNUSED_PARAMETER")

package phoupraw.mcmod.linked.minecraft.block

import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import phoupraw.mcmod.linked.minecraft.block.entity.TickableBlockEntity
import phoupraw.mcmod.linked.mixin.minecraft.ABlockWithEntity

/**
 * @see TickableBlockEntity
 */
abstract class BlockEntityBlock(settings: Settings) : NameableBlock(settings), BlockEntityProvider {
    override fun onSyncedBlockEvent(state: BlockState, world: World, pos: BlockPos, type: Int, data: Int): Boolean {
        val blockEntity = world.getBlockEntity(pos)
        return super.onSyncedBlockEvent(state, world, pos, type, data) or (blockEntity != null && blockEntity.onSyncedBlockEvent(type, data))
    }

    override fun <T : BlockEntity> getTicker(world: World, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
        return super.getTicker(world, state, type)
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        return ScreenHandlerBlock.onUse(this, state, world, pos, player, hand, hit)
    }

    override fun createScreenHandlerFactory(state: BlockState, world: World, pos: BlockPos): NamedScreenHandlerFactory? {
        return ScreenHandlerBlock.createScreenHandlerFactory(this, state, world, pos)
    }

    abstract override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? //public abstract BlockEntityType<? extends E> getBlockEntityType();

    companion object {
        fun <T> getTicker(self: Block, world: World, state: BlockState, type: BlockEntityType<T>, expectedType: BlockEntityType<T>): BlockEntityTicker<T>? where T : BlockEntity, T : TickableBlockEntity {
            return  /* world.isClient() ? null :*/ABlockWithEntity.invokeCheckType(type, expectedType, TickableBlockEntity::tick)
        }

        fun <T> getTickerServer(self: Block, world: World, state: BlockState, type: BlockEntityType<T>, expectedType: BlockEntityType<T>): BlockEntityTicker<T>? where T : BlockEntity, T : TickableBlockEntity {
            return if (world.isClient()) null else getTicker(self, world, state, type, expectedType)
        }
    }
}
