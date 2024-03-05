package phoupraw.mcmod.linked.fabric.transfer.base

import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import phoupraw.mcmod.linked.fabric.transfer.storage.empty
import java.util.*

interface SimpleSingleSlotStorage<T> : SingleSlotStorage<T> {
    override var resource: T
    override var amount: Long
    override fun nonEmptyIterator(): Iterator<SingleSlotStorage<T>> = if (empty) Collections.emptyIterator() else iterator()
}