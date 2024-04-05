@file:Suppress("OVERRIDE_DEPRECATION", "DEPRECATION", "UNUSED_PARAMETER")

package phoupraw.mcmod.linked.minecraft.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class ScreenHandlerBlock(settings: Settings) : NameableBlock(settings) {
    override fun createScreenHandlerFactory(state: BlockState, world: World, pos: BlockPos): NamedScreenHandlerFactory? {
        return createScreenHandlerFactory(this, state, world, pos)
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        return onUse(this, state, world, pos, player, hand, hit)
    }

    companion object {
        fun createScreenHandlerFactory(self: Block, state: BlockState, world: World, pos: BlockPos): NamedScreenHandlerFactory? {
            return world.getBlockEntity(pos) as? NamedScreenHandlerFactory
        }

        fun onUse(self: Block, state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
            val factory = self.createScreenHandlerFactory(state, world, pos) ?: return ActionResult.PASS
            player.openHandledScreen(factory)
            return ActionResult.SUCCESS
        }
    }
}
