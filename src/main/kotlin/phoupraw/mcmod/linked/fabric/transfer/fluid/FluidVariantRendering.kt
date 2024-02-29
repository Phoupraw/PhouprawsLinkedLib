@file:Environment(EnvType.CLIENT)

package phoupraw.mcmod.linked.fabric.transfer.fluid

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.client.item.TooltipContext
import net.minecraft.client.texture.Sprite
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockRenderView

val FluidVariant.tooltip: List<Text> get() = FluidVariantRendering.getTooltip(this)
fun FluidVariant.getTooltip(context: TooltipContext): List<Text> = FluidVariantRendering.getTooltip(this, context)
val FluidVariant.sprites: Array<Sprite>? get() = FluidVariantRendering.getSprites(this) /*as Array<Sprite>?*/
val FluidVariant.sprite: Sprite? get() = FluidVariantRendering.getSprite(this)
val FluidVariant.color get() = FluidVariantRendering.getColor(this)
fun FluidVariant.getColor(view: BlockRenderView? = null, pos: BlockPos? = null) = FluidVariantRendering.getColor(this, view, pos)
