@file:Suppress("INAPPLICABLE_JVM_NAME")

package phoupraw.mcmod.linked.fabric.transfer.energy

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import phoupraw.mcmod.linked.fabric.transfer.GEntityApiLookup
import phoupraw.mcmod.linked.fabric.transfer.base.SingletonVariant
import phoupraw.mcmod.linked.fabric.transfer.storage.Storages

//import phoupraw.mcmod.xp_obelisk.transfer.energy.EnergySingleton;
object Energy : SingletonVariant() {
    const val EU = FluidConstants.BUCKET
    @JvmField
    val ENTITY = GEntityApiLookup<Storage<Energy>, Vec3d?>(Identifier("generic_api", "xp/entity"))
    @JvmStatic
    fun move(from: Storage<Energy>?, to: Storage<Energy>?, maxAmount: Long = Long.MAX_VALUE, transaction: TransactionContext? = null): Long {
        return Storages.move(from, to, Energy, maxAmount, transaction)
    }
}
