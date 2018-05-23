package jp.cheerapps.howmanybiscuits.data

import kotlin.math.sqrt

data class Vector (var x: Float, var y: Float){
    operator fun unaryMinus() = Vector(-x, -y)

    operator fun plus(v: Vector) = Vector(x + v.x, y + v.y)

    operator fun minus(v: Vector) = Vector(x - v.x, y - v.y)

    operator fun times(f: Float) = Vector(x * f, y * f)

    fun length() = sqrt(length2())

    fun length2() = x * x + y * y

    fun unit(): Vector {
        val len = length()
        return Vector(x / len, y / len)
    }

    /**
     * 内積
     */
    infix fun dot(v: Vector) = x * v.x + y * v.y
}