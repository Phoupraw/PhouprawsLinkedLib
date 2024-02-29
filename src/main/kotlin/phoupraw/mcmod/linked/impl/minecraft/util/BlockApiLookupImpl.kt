@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.impl.minecraft.util

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import phoupraw.mcmod.linked.api.minecraft.util.BlockApiFinder
import phoupraw.mcmod.linked.api.minecraft.util.BlockApiLookup
import phoupraw.mcmod.linked.api.util.MutableEvent

class BlockApiLookupImpl<C, A>(override val name: String) : BlockApiLookup<C, A> {
    override val preliminary: MutableEvent<BlockApiFinder<BlockEntity?, C, A>> = newEvent()
    @get:JvmName("_blockSpecific")
    val _blockSpecific: MutableMap<Block, MutableEvent<BlockApiFinder<BlockEntity?, C, A>>> = mutableMapOf()
    override operator fun get(block: Block): MutableEvent<BlockApiFinder<BlockEntity?, C, A>> = _blockSpecific.getOrPut(block) { newEvent() }
    override val blockSpecific: Map<Block, MutableEvent<BlockApiFinder<BlockEntity?, C, A>>> get() = _blockSpecific
    @get:JvmName("_entitySpecific")
    val _entitySpecific: MutableMap<BlockEntityType<*>, MutableEvent<BlockApiFinder<BlockEntity, C, A>>> = mutableMapOf()
    @Suppress("UNCHECKED_CAST")
    override operator fun <E : BlockEntity> get(type: BlockEntityType<out E>): MutableEvent<BlockApiFinder<E, C, A>> = _entitySpecific.getOrPut(type) { newEvent() } as MutableEvent<BlockApiFinder<E, C, A>>
    override val entitySpecific: Map<BlockEntityType<*>, MutableEvent<BlockApiFinder<BlockEntity, C, A>>> get() = _entitySpecific
    override val fallback: MutableEvent<BlockApiFinder<BlockEntity?, C, A>> = newEvent()
    override var find: BlockApiFinder<BlockEntity?, C, A> = BlockApiFinder(::defaultFind)
    fun defaultFind(world: World, blockPos: BlockPos, blockState: BlockState, blockEntity: BlockEntity?, context: C): A? {
        return preliminary.call(world, blockPos, blockState, blockEntity, context)
          ?: blockSpecific[blockState.block]?.call?.invoke(world, blockPos, blockState, blockEntity, context)
          ?: blockEntity?.run { entitySpecific[type]?.call?.invoke(world, blockPos, blockState, blockEntity, context) }
          ?: fallback.call(world, blockPos, blockState, blockEntity, context)
    }

    fun <E> newEvent(): MutableEvent<BlockApiFinder<E, C, A>> = MutableEvent { finds ->
        BlockApiFinder { world, blockPos, blockState, blockEntity, context ->
            finds.firstNotNullOfOrNull { it(world, blockPos, blockState, blockEntity, context) }
        }
    }
}