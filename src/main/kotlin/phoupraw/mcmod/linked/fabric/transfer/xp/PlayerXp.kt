package phoupraw.mcmod.linked.fabric.transfer.xp

import net.minecraft.entity.player.PlayerEntity
import phoupraw.mcmod.linked.api.fabric.transfer.xp.XpLevel
import phoupraw.mcmod.linked.api.fabric.transfer.xp.XpValue
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

private inline fun piecewise(v: Double, t1: Double, t2: Double, calc: (a: Double, b: Double, c: Double) -> Double): Double {
    val a: Double
    val b: Double
    val c: Double
    if (v < t1) {
        a = 1.0
        b = 6.0
        c = 0.0
    } else if (v < t2) {
        a = 2.5
        b = -40.5
        c = 360.0
    } else {
        a = 4.5
        b = -162.5
        c = 2220.0
    }
    return calc(a, b, c)
}
@Deprecated("移动到phoupraw.mcmod.linked.api")
@JvmInline
value class XpValue(val v: Double) {
    constructor(player: PlayerEntity) : this(XpLevel(player).value.v)

    val level get() = XpLevel(piecewise(v, 352.0, 1507.0) { a, b, c -> (-b + sqrt(b * b - 4 * a * (c - v))) / (2 * a) })
    val floor get() = v.toLong()
    operator fun minus(value: XpValue) = XpValue(v - value.v)
}
@Deprecated("移动到phoupraw.mcmod.linked.api")
var PlayerEntity.xpValue: XpValue
    get() = XpValue(this)
    set(value) {
        xpLevel = value.level
    }
@Deprecated("移动到phoupraw.mcmod.linked.api")
var PlayerEntity.xpLevel: XpLevel
    get() = XpLevel(this)
    set(value) {
        val (i, d) = value
        experienceLevel = i
        experienceProgress = d
    }
@Deprecated("移动到phoupraw.mcmod.linked.api")
@JvmInline
value class XpLevel(val v: Double) : Comparable<XpLevel> {
    constructor(player: PlayerEntity) : this(player.experienceLevel + player.experienceProgress.toDouble())

    val value get() = XpValue(piecewise(v, 16.0, 31.0) { a, b, c -> a * v * v + b * v + c })
    val next get() = XpLevel(floor(v + 1))
    val last get() = XpLevel(floor(v - 1))
    //    val restToNextLevel
    //        get() = nextLevel-this
    val present get() = XpLevel(floor(v))
    val floor get() = value.floor
    val ceil get() = ceil(value.v).toLong()
    //    fun toValueToNextLevel() = XpValue(ceil(XpLevel(floor(v + 1)).toValue().v - toValue().v))
    //    fun toValueFloorOrDecrement(): XpValue {
    //        val currentExp = toValue()
    //        val deltaExp = floor(currentExp.m - XpLevel(floor(m)).toValue())
    //        return if (deltaExp != 0.0) deltaExp else floor(currentExp - PlayerExpStorage.toValue(floor(level) - 1)).toLong()
    //    }
    operator fun component1() = v.toInt()
    operator fun component2() = (v - floor(v)).toFloat()
    operator fun minus(level: XpLevel): XpLevel = (value - level.value).level
    override fun compareTo(other: XpLevel): Int = v.compareTo(other.v)
}