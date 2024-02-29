package phoupraw.mcmod.linked.minecraft.block.entity

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text
import net.minecraft.util.Nameable
import net.minecraft.util.math.BlockPos
import phoupraw.mcmod.linked.minecraft.customName

abstract class NameableBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : BlockEntity(type, pos, state), Nameable {
    @get:JvmName("stupidKotlin_getCustomName")
    //    @get:JvmName("getCustomName")
    var customName: Text? = null
        set(value) {
            val changed = field != value
            field = value
            if (changed) {
                markDirty()
            }
        }

    override fun getCustomName(): Text? = customName
    override fun getName(): Text = customName ?: cachedState.block.name
    override fun readNbt(root: NbtCompound) {
        super.readNbt(root)
        customName = root.customName
    }

    override fun writeNbt(root: NbtCompound) {
        super.writeNbt(root)
        root.customName = customName
    }
}
