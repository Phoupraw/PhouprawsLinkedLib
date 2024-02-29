package phoupraw.mcmod.linked.minecraft

import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EntityType
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.potion.Potion
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.World

//val StatusEffect.id:Identifier? get() = Registries.STATUS_EFFECT.getId(this)
fun <T> Registry<in T>.register(id: Identifier, value: T): T = Registry.register(this, id, value)
val Block.id: Identifier get() = Registries.BLOCK.getId(this)
fun Block(id: Identifier): Block = Registries.BLOCK.get(id)
val Item.id: Identifier get() = Registries.ITEM.getId(this)
fun Item(id: Identifier): Item = Registries.ITEM.get(id)
val Fluid.id: Identifier get() = Registries.FLUID.getId(this)
fun Fluid(id: Identifier): Fluid = Registries.FLUID.get(id)
val EntityType<*>.id: Identifier get() = Registries.ENTITY_TYPE.getId(this)
val Potion.id: Identifier get() = Registries.POTION.getId(this)
fun Potion(id: Identifier): Potion = Registries.POTION.get(id)
val BlockEntityType<*>.id: Identifier? get() = Registries.BLOCK_ENTITY_TYPE.getId(this)
val Enchantment.id: Identifier? get() = Registries.ENCHANTMENT.getId(this)
fun Enchantment(id: Identifier): Enchantment? = Registries.ENCHANTMENT.get(id)
val StatusEffect.id: Identifier? get() = Registries.STATUS_EFFECT.getId(this)
fun StatusEffect(id: Identifier): StatusEffect? = Registries.STATUS_EFFECT.get(id)
val RecipeType<*>.id: Identifier? get() = Registries.RECIPE_TYPE.getId(this)
/**
 * 傻逼Kotlin
 */
fun VoxelShape(xMin: Int, yMin: Int, zMin: Int, xMax: Int, yMax: Int, zMax: Int): VoxelShape = Block.createCuboidShape(xMin.toDouble(), yMin.toDouble(), zMin.toDouble(), xMax.toDouble(), yMax.toDouble(), zMax.toDouble())
fun World.playSound(except: PlayerEntity, sound: SoundEvent, category: SoundCategory = SoundCategory.PLAYERS, volume: Float = 1f, pitch: Float = 1f) = playSound(except, except.x, except.y + except.height / 2, except.z, sound, category, volume, pitch)
fun Identifier.toShortString(): String = if (namespace == Identifier.DEFAULT_NAMESPACE) path else toString()