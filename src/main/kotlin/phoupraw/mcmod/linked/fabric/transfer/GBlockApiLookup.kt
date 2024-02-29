package phoupraw.mcmod.linked.fabric.transfer

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import phoupraw.mcmod.linked.lang.KClass

@Deprecated("与GBlockApiLookup一同废弃", ReplaceWith("phoupraw.mcmod.linked.api.minecraft.util.BlockApiFinder"))
typealias GBlockApiFind<E, A, C> = (world: World, blockPos: BlockPos, blockState: BlockState, blockEntity: E, context: C) -> A?

@Deprecated("与GenericApiLookup一同废弃", ReplaceWith("phoupraw.mcmod.linked.api.minecraft.util.BlockApiLookup"))
class GBlockApiLookup<A, C>(val id: Identifier) : GBlockApiFind<BlockEntity?, A, C> {
    val preliminary: Event<GBlockApiFind<BlockEntity?, A, C>> = newEvent()
    val fallback: Event<GBlockApiFind<BlockEntity?, A, C>> = newEvent()
    val blockSpecific: Map<Block, Event<GBlockApiFind<BlockEntity?, A, C>>>
        get() = _blockSpecific
    private val _blockSpecific: MutableMap<Block, Event<GBlockApiFind<BlockEntity?, A, C>>> = mutableMapOf()
    val entitySpecific: Map<BlockEntityType<*>, Event<GBlockApiFind<BlockEntity, A, C>>>
        get() = _entitySpecific
    private val _entitySpecific: MutableMap<BlockEntityType<*>, Event<GBlockApiFind<BlockEntity, A, C>>> = mutableMapOf()
    operator fun get(block: Block) = _blockSpecific.getOrPut(block) { newEvent() }
    fun register(vararg blocks: Block, find: GBlockApiFind<BlockEntity?, A, C>) {
        for (block in blocks) {
            this[block].register(find)
        }
    }
    @Suppress("UNCHECKED_CAST")
    operator fun <T : BlockEntity> get(type: BlockEntityType<out T>): Event<GBlockApiFind<T, A, C>> = _entitySpecific.getOrPut(type) { newEvent() } as Event<GBlockApiFind<T, A, C>>
    fun <T : BlockEntity> register(vararg types: BlockEntityType<out T>, find: GBlockApiFind<T, A, C>) {
        for (type in types) {
            this[type].register(find)
        }
    }

    fun invoke(world: World, blockPos: BlockPos, context: C): A? = invoke(world, blockPos, world.getBlockState(blockPos), context)
    fun invoke(world: World, blockPos: BlockPos, blockState: BlockState, context: C): A? = invoke(world, blockPos, blockState, if (blockState.hasBlockEntity()) world.getBlockEntity(blockPos) else null, context)
    //    @JvmOverloads
    //    fun invoke(world: World, blockPos: BlockPos, context: C, blockState: BlockState = world.getBlockState(blockPos), blockEntity: BlockEntity? = if (blockState.hasBlockEntity()) world.getBlockEntity(blockPos) else null): A? = invoke(world, blockPos, blockState, blockEntity, context)
    override fun invoke(world: World, blockPos: BlockPos, blockState: BlockState, blockEntity: BlockEntity?, context: C): A? {
        if (blockState.hasBlockEntity() && blockEntity == null) {
            world.getBlockEntity(blockPos)?.also { return invoke(world, blockPos, blockState, it, context) }
        }
        preliminary.invoker()?.invoke(world, blockPos, blockState, blockEntity, context)?.also { return it }
        blockSpecific[blockState.block]?.invoker()?.invoke(world, blockPos, blockState, blockEntity, context)?.also { return it }
        blockEntity?.apply { entitySpecific[type]?.invoker()?.invoke(world, blockPos, blockState, blockEntity, context)?.also { return it } }
        fallback.invoker()?.invoke(world, blockPos, blockState, blockEntity, context)?.also { return it }
        return null
    }

    private fun <T /*: BlockEntity?*/> newEvent(): Event<GBlockApiFind<T, A, C>> {
        return EventFactory.createArrayBacked(KClass<GBlockApiFind<T, A, C>>().java) { finds ->
            invoker@{ world, blockPos, blockState, blockEntity, context ->
                for (find in finds) {
                    find(world, blockPos, blockState, blockEntity, context)?.also { return@invoker it }
                }
                null
            }
        }
    }
}
