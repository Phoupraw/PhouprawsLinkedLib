@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.api.minecraft.util

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import phoupraw.mcmod.linked.api.util.MutableEvent
import phoupraw.mcmod.linked.impl.minecraft.util.BlockApiLookupImpl

interface BlockApiLookup<C, A> {
    @get:JvmName("name")
    val name: String
    @get:JvmName("preliminary")
    val preliminary: MutableEvent<BlockApiFinder<BlockEntity?, C, A>>
    operator fun get(block: Block): MutableEvent<BlockApiFinder<BlockEntity?, C, A>>
    fun register(vararg blocks: Block, find: BlockApiFinder<BlockEntity?, C, A>) {
        for (block in blocks) {
            this[block].register(find)
        }
    }
    @get:JvmName("blockSpecific")
    val blockSpecific: Map<Block, MutableEvent<BlockApiFinder<BlockEntity?, C, A>>>
    operator fun <E : BlockEntity> get(type: BlockEntityType<out E>): MutableEvent<BlockApiFinder<E, C, A>>
    fun <E : BlockEntity> register(vararg types: BlockEntityType<out E>, find: BlockApiFinder<E, C, A>) {
        for (type in types) {
            this[type].register(find)
        }
    }
    @get:JvmName("entitySpecific")
    val entitySpecific: Map<BlockEntityType<*>, MutableEvent<BlockApiFinder<BlockEntity, C, A>>>
    @get:JvmName("fallback")
    val fallback: MutableEvent<BlockApiFinder<BlockEntity?, C, A>>
    @get:JvmName("find")
    var find: BlockApiFinder<BlockEntity?, C, A>
    fun find(world: World, blockPos: BlockPos, blockState: BlockState, context: C): A? = find(world, blockPos, blockState, if (blockState.hasBlockEntity()) world.getBlockEntity(blockPos) else null, context)
    fun find(world: World, blockPos: BlockPos, context: C): A? = find(world, blockPos, world.getBlockState(blockPos), context)

    companion object {
        @JvmStatic
        operator fun <A, C> invoke(name: String): BlockApiLookup<A, C> = BlockApiLookupImpl(name)
    }
}