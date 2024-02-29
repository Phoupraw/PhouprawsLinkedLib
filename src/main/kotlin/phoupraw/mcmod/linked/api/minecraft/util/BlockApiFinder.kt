package phoupraw.mcmod.linked.api.minecraft.util

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

fun interface BlockApiFinder<in E, in C, out A> : (World, BlockPos, BlockState, E, C) -> A? {
    override fun invoke(world: World, blockPos: BlockPos, blockState: BlockState, blockEntity: E, context: C): A?
}