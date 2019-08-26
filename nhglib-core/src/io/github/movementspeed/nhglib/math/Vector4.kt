package io.github.movementspeed.nhglib.math

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector
import com.badlogic.gdx.utils.NumberUtils
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Created by Fausto Napoli on 01/03/2017.
 */
class Vector4 : Vector<Vector4> {
    var x: Float = 0f
    var y: Float = 0f
    var z: Float = 0f
    var k: Float = 0f

    constructor(x: Float, y: Float, z: Float, k: Float) {
        this[x, y, z] = k
    }

    constructor(vector: Vector4) {
        this.set(vector)
    }

    override fun cpy(): Vector4 {
        return Vector4(this)
    }

    override fun len(): Float {
        return sqrt((x * x + y * y + z * z + k * k).toDouble()).toFloat()
    }

    override fun len2(): Float {
        return x * x + y * y + z * z + k * k
    }

    override fun limit(limit: Float): Vector4 {
        return limit2(limit * limit)
    }

    override fun limit2(limit2: Float): Vector4 {
        val len2 = len2()

        if (len2 > limit2) {
            scl(Math.sqrt((limit2 / len2).toDouble()).toFloat())
        }

        return this
    }

    override fun setLength(len: Float): Vector4 {
        return setLength2(len * len)
    }

    override fun setLength2(len2: Float): Vector4 {
        val oldLen2 = len2()
        return if (oldLen2 == 0f || oldLen2 == len2) this else scl(Math.sqrt((len2 / oldLen2).toDouble()).toFloat())
    }

    override fun clamp(min: Float, max: Float): Vector4 {
        val len2 = len2()
        if (len2 == 0f) return this
        val max2 = max * max
        if (len2 > max2) return scl(Math.sqrt((max2 / len2).toDouble()).toFloat())
        val min2 = min * min
        return if (len2 < min2) scl(Math.sqrt((min2 / len2).toDouble()).toFloat()) else this

    }

    override fun set(v: Vector4): Vector4 {
        this[v.x, v.y, v.z] = v.k
        return this
    }

    override fun sub(v: Vector4): Vector4 {
        return this.sub(v.x, v.y, v.z, v.k)
    }

    override fun nor(): Vector4 {
        val len2 = this.len2()
        return if (len2 == 0f || len2 == 1f) this else this.scl(1f / Math.sqrt(len2.toDouble()).toFloat())
    }

    override fun add(v: Vector4): Vector4 {
        return this.add(v.x, v.y, v.z, v.k)
    }

    override fun dot(v: Vector4): Float {
        return x * v.x + y * v.y + z * v.z + k * v.k
    }

    override fun scl(scalar: Float): Vector4 {
        return this.set(this.x * scalar, this.y * scalar, this.z * scalar, this.k * scalar)
    }

    override fun scl(v: Vector4): Vector4 {
        return this.set(x * v.x, y * v.y, z * v.z, k * v.k)
    }

    override fun dst(v: Vector4): Float {
        val a = v.x - x
        val b = v.y - y
        val c = v.z - z
        val d = v.k - k

        return Math.sqrt((a * a + b * b + c * c + d * d).toDouble()).toFloat()
    }

    override fun dst2(v: Vector4): Float {
        val a = v.x - x
        val b = v.y - y
        val c = v.z - z
        val d = v.k - k

        return a * a + b * b + c * c + d * d
    }

    override fun lerp(target: Vector4, alpha: Float): Vector4 {
        x += alpha * (target.x - x)
        y += alpha * (target.y - y)
        z += alpha * (target.z - z)
        k += alpha * (target.k - k)

        return this
    }

    override fun interpolate(target: Vector4, alpha: Float, interpolator: Interpolation): Vector4 {
        return lerp(target, interpolator.apply(0f, 1f, alpha))
    }

    override fun setToRandomDirection(): Vector4 {
        return Vector4(0f, 0f, 0f, 0f)
    }

    override fun isUnit(): Boolean {
        return isUnit(0.000000001f)
    }

    override fun isUnit(margin: Float): Boolean {
        return Math.abs(len2() - 1f) < margin
    }

    override fun isZero(): Boolean {
        return x == 0f && y == 0f && z == 0f && k == 0f
    }

    override fun isZero(margin: Float): Boolean {
        return len2() < margin
    }

    override fun isOnLine(other: Vector4, epsilon: Float): Boolean {
        return false
    }

    override fun isOnLine(other: Vector4): Boolean {
        return false
    }

    override fun isCollinear(other: Vector4, epsilon: Float): Boolean {
        return false
    }

    override fun isCollinear(other: Vector4): Boolean {
        return false
    }

    override fun isCollinearOpposite(other: Vector4, epsilon: Float): Boolean {
        return false
    }

    override fun isCollinearOpposite(other: Vector4): Boolean {
        return false
    }

    override fun isPerpendicular(other: Vector4): Boolean {
        return MathUtils.isZero(dot(other))
    }

    override fun isPerpendicular(other: Vector4, epsilon: Float): Boolean {
        return MathUtils.isZero(dot(other), epsilon)
    }

    override fun hasSameDirection(other: Vector4): Boolean {
        return dot(other) > 0
    }

    override fun hasOppositeDirection(other: Vector4): Boolean {
        return dot(other) < 0
    }

    override fun epsilonEquals(other: Vector4?, epsilon: Float): Boolean {
        if (other == null) return false
        if (abs(other.x - x) > epsilon) return false
        if (abs(other.y - y) > epsilon) return false
        if (abs(other.z - z) > epsilon) return false
        return abs(other.k - k) <= epsilon
    }

    override fun mulAdd(vec: Vector4, scalar: Float): Vector4 {
        this.x += vec.x * scalar
        this.y += vec.y * scalar
        this.z += vec.z * scalar
        this.k += vec.k * scalar

        return this
    }

    override fun mulAdd(vec: Vector4, mulVec: Vector4): Vector4 {
        this.x += vec.x * mulVec.x
        this.y += vec.y * mulVec.y
        this.z += vec.z * mulVec.z
        this.k += vec.k * mulVec.k

        return this
    }

    override fun setZero(): Vector4 {
        return set(0f, 0f, 0f, 0f)
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + NumberUtils.floatToIntBits(x)
        result = prime * result + NumberUtils.floatToIntBits(y)
        result = prime * result + NumberUtils.floatToIntBits(z)
        result = prime * result + NumberUtils.floatToIntBits(k)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        val vec4 = other as? Vector4

        vec4?.let {
            if (NumberUtils.floatToIntBits(x) != NumberUtils.floatToIntBits(it.x)) return false
            if (NumberUtils.floatToIntBits(y) != NumberUtils.floatToIntBits(it.y)) return false
            if (NumberUtils.floatToIntBits(z) != NumberUtils.floatToIntBits(it.z)) return false
            return NumberUtils.floatToIntBits(k) == NumberUtils.floatToIntBits(it.k)
        }

        return false
    }

    operator fun set(x: Float, y: Float, z: Float, k: Float): Vector4 {
        this.x = x
        this.y = y
        this.z = z
        this.k = k

        return this
    }

    /**
     * Subtracts the other vector from this vector.
     *
     * @param x The x-component of the other vector
     * @param y The y-component of the other vector
     * @param z The z-component of the other vector
     * @return This vector for chaining
     */
    fun sub(x: Float, y: Float, z: Float, k: Float): Vector4 {
        return this.set(this.x - x, this.y - y, this.z - z, this.k - k)
    }

    /**
     * Subtracts the given value from all components of this vector
     *
     * @param value The value
     * @return This vector for chaining
     */
    fun sub(value: Float): Vector4 {
        return this.set(this.x - value, this.y - value, this.z - value, this.k - value)
    }

    /**
     * Adds the given vector to this component
     *
     * @param x The x-component of the other vector
     * @param y The y-component of the other vector
     * @param z The z-component of the other vector
     * @return This vector for chaining.
     */
    fun add(x: Float, y: Float, z: Float, k: Float): Vector4 {
        return this.set(this.x + x, this.y + y, this.z + z, this.k + k)
    }

    /**
     * Adds the given value to all three components of the vector.
     *
     * @param values The value
     * @return This vector for chaining
     */
    fun add(values: Float): Vector4 {
        return this.set(this.x + values, this.y + values, this.z + values, this.k + values)
    }

    /**
     * Scales this vector by the given values
     *
     * @param vx X value
     * @param vy Y value
     * @param vz Z value
     * @return This vector for chaining
     */
    fun scl(vx: Float, vy: Float, vz: Float, kz: Float): Vector4 {
        return this.set(this.x * vx, this.y * vy, this.z * vz, this.k * kz)
    }

    companion object {
        val X = Vector4(1f, 0f, 0f, 0f)
        val Y = Vector4(0f, 1f, 0f, 0f)
        val Z = Vector4(0f, 0f, 1f, 0f)
        val K = Vector4(0f, 0f, 0f, 1f)
        val Zero = Vector4(0f, 0f, 0f, 0f)
    }
}
