package phoupraw.mcmod.linked.api.minecraft.misc

import net.minecraft.util.math.random.Random
import org.jetbrains.annotations.Contract
import kotlin.math.floor

object Math2 {
    @Contract(mutates = "param2")
    @JvmOverloads
    @JvmStatic
    fun twoPoint(p: Double, random: Random = Random.createLocal()): Double {
        val integer = floor(p)
        val decimal = p - integer
        return if (decimal >= random.nextDouble()) integer + 1 else integer
    }
}