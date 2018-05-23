package jp.cheerapps.howmanybiscuits.views.custom.component

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
    private var divisionCount = 1
    private val biscuitColor = random.nextInt() or 0xFF000000.toInt()

    init {
        val rf = random.nextFloat()
        divisionCount = countDist.first { (p, _) -> rf <= p }.second
    }

    fun draw(canvas: Canvas) {
        val paint = Paint().apply { color = biscuitColor }
        canvas.drawCircle(p.x, p.y, r, paint)
    }

    fun move() {
        p += v
    }

    fun restitutionBiscuit(biscuits: List<Biscuit>) {
        biscuits.forEach {
            if (it != this && collides(it)) {
                v = restitute(it)
            }
        }
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
                            v = Vector(rand(5f, 5f, true), rand(5f, 5f, true)),
                            r = r / sqrt(divisionCount.toFloat()),
                            random = random)
                }

    fun inside(point: Vector) = (point - p).length2() <= r * r

    private fun collides(biscuit: Biscuit) = (biscuit.p - p).length2() <= (biscuit.r + r) * (biscuit.r + r)

    private fun restitute(biscuit: Biscuit): Vector {
        val pv = (biscuit.p - p)
        if (pv dot v < 0) return v

        val pu = pv.unit()
        return v - pu * (2 * (pu dot v))
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