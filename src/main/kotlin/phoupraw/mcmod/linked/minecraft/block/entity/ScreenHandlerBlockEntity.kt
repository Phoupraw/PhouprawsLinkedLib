package phoupraw.mcmod.linked.minecraft.block.entity

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos

abstract class ScreenHandlerBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : NameableBlockEntity(type, pos, state), NamedScreenHandlerFactory {
    override fun shouldCloseCurrentScreen(): Boolean = false
    override fun getDisplayName(): Text = super.getDisplayName()
    //    override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? {
    //        return super.toUpdatePacket()
    //    }
    abstract override fun createMenu(syncId: Int, playerInv: PlayerInventory, player: PlayerEntity): ScreenHandler?
}
