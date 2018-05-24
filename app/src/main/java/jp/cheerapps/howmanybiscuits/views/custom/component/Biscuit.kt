package jp.cheerapps.howmanybiscuits.views.custom.component

import jp.cheerapps.howmanybiscuits.data.Vector
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

class Biscuit (
        // TODO: Vectorクラスを使わないで書いてみる
    var p: Vector,
    var v: Vector,
    var r: Float,
    private val random: Random
) {
    data class Data(val center: Vector, val size: Int, val angle: Float)
    private val divisionCount: Int
    private var angle = random.nextInt(360).toFloat()
    private val dAngle = v.length2() / 15 * (if (random.nextBoolean()) 1 else -1)

    init {
        val prob = random.nextFloat()
        divisionCount = countDist.first { (p, _) -> prob <= p }.second
    }

    fun generateData(): Data {
        return Data(p, (2 * r).toInt(), angle)
    }

    fun move() {
        p += v
        angle = (angle + dAngle) % 360
    }

    fun restitutionBiscuit(biscuits: List<Biscuit>) {
        biscuits.dropWhile { it != this }
                .drop(1)
                .forEach { if (collides(it)) restitute(it) }
    }

    fun restitutionWall(size: Vector) {
        when {
            p.x - r < 0       -> v.x =  abs(v.x)
            size.x <= p.x + r -> v.x = -abs(v.x)
            p.y - r < 0       -> v.y =  abs(v.y)
            size.y <= p.y + r -> v.y = -abs(v.y)
        }
    }

    fun divide(): List<Biscuit> =
            if (divisionCount == 1) listOf(Biscuit(p, v, r, random))
            else (0 until divisionCount)
                .map {
                    Biscuit(p = Vector(p.x + rand(r / 2, r, true), p.y + + rand(r / 2, r, true)),
                            v = Vector(rand(3f, 5f, true), rand(3f, 5f, true)),
                            r = r / sqrt(divisionCount.toFloat()),
                            random = random)
                }

    fun inside(point: Vector) = (point - p).length2() <= r * r

    private fun collides(biscuit: Biscuit) = (biscuit.p - p).length2() <= (biscuit.r + r) * (biscuit.r + r)

    private fun restitute(biscuit: Biscuit) {
        val pv = (biscuit.p - p)
        if (pv dot v <= 0 && pv dot biscuit.v >= 0) return

        val pu = pv.unit()
        if (pv dot v  > 0) v -= pu * (2 * (pu dot v))
        if (pv dot biscuit.v < 0) biscuit.v -= pu * (2 * (pu dot biscuit.v))
    }

    private fun rand(min: Float, max: Float, minus: Boolean = true): Float {
        val ret = random.nextFloat() * (max - min) + min
        return if (minus && random.nextBoolean()) -ret else ret
    }

    companion object {
        private val TAG = Biscuit::class.java.simpleName
        private val countDist = listOf(0.2f to 1, 0.5f to 2, 1.0f to 3)
    }
}