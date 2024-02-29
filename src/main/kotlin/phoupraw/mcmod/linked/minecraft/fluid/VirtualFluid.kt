package phoupraw.mcmod.linked.minecraft.fluid

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.fluid.FlowableFluid
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.FluidState
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView

open class VirtualFluid : FlowableFluid() {
    public override fun getBlastResistance(): Float = 0f
    override fun isStill(state: FluidState): Boolean = true
    public override fun canBeReplacedWith(fluidState: FluidState, blockView: BlockView, blockPos: BlockPos, fluid: Fluid, direction: Direction): Boolean = false
    override fun getTickRate(worldView: WorldView): Int = 0
    override fun getStill(): Fluid = this
    public override fun toBlockState(state: FluidState): BlockState = Blocks.AIR.defaultState
    override fun matchesType(fluid: Fluid): Boolean = fluid === this
    override fun getFlowing(): Fluid = this
    public override fun isInfinite(world: World): Boolean = false
    public override fun beforeBreakingBlock(world: WorldAccess, pos: BlockPos, state: BlockState) = Unit
    public override fun getFlowSpeed(worldView: WorldView): Int = 0
    public override fun getLevelDecreasePerBlock(worldView: WorldView): Int = 0
    override fun getLevel(state: FluidState): Int = 0
    override fun getBucketItem(): Item = Items.AIR
}
