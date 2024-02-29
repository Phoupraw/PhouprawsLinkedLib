package phoupraw.mcmod.linked.minecraft.fluid

import net.minecraft.item.Item

class BucketableVirtualFluid(private val bucketItem: Item) : VirtualFluid() {
    override fun getBucketItem(): Item = bucketItem
}
