package io.github.voidzombie.nhglib.math;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.utils.NumberUtils;

/**
 * Created by Fausto Napoli on 01/03/2017.
 */
public class Vector4 implements Vector<Vector4> {
    public final static Vector4 X = new Vector4(1, 0, 0, 0);
    public final static Vector4 Y = new Vector4(0, 1, 0, 0);
    public final static Vector4 Z = new Vector4(0, 0, 1, 0);
    public final static Vector4 K = new Vector4(0, 0, 0, 1);
    public final static Vector4 Zero = new Vector4(0, 0, 0, 0);

    public float x;
    public float y;
    public float z;
    public float k;

    public Vector4(float x, float y, float z, float k) {
        this.set(x, y, z, k);
    }

    public Vector4(Vector4 vector) {
        this.set(vector);
    }

    @Override
    public Vector4 cpy() {
        return new Vector4(this);
    }

    @Override
    public float len() {
        return (float) Math.sqrt(x * x + y * y + z * z + k * k);
    }

    @Override
    public float len2() {
        return x * x + y * y + z * z + k * k;
    }

    @Override
    public Vector4 limit(float limit) {
        return limit2(limit * limit);
    }

    @Override
    public Vector4 limit2(float limit2) {
        float len2 = len2();

        if (len2 > limit2) {
            scl((float) Math.sqrt(limit2 / len2));
        }

        return this;
    }

    @Override
    public Vector4 setLength(float len) {
        return setLength2(len * len);
    }

    @Override
    public Vector4 setLength2(float len2) {
        float oldLen2 = len2();
        return (oldLen2 == 0 || oldLen2 == len2) ? this : scl((float) Math.sqrt(len2 / oldLen2));
    }

    @Override
    public Vector4 clamp(float min, float max) {
        final float len2 = len2();
        if (len2 == 0f) return this;
        float max2 = max * max;
        if (len2 > max2) return scl((float) Math.sqrt(max2 / len2));
        float min2 = min * min;
        if (len2 < min2) return scl((float) Math.sqrt(min2 / len2));

        return this;
    }

    @Override
    public Vector4 set(Vector4 v) {
        this.set(v.x, v.y, v.z, v.k);
        return this;
    }

    @Override
    public Vector4 sub(Vector4 v) {
        return this.sub(v.x, v.y, v.z, v.k);
    }

    @Override
    public Vector4 nor() {
        final float len2 = this.len2();
        if (len2 == 0f || len2 == 1f) return this;
        return this.scl(1f / (float) Math.sqrt(len2));
    }

    @Override
    public Vector4 add(Vector4 v) {
        return this.add(v.x, v.y, v.z, v.k);
    }

    @Override
    public float dot(Vector4 v) {
        return x * v.x + y * v.y + z * v.z + k * v.k;
    }

    @Override
    public Vector4 scl(float scalar) {
        return this.set(this.x * scalar, this.y * scalar, this.z * scalar, this.k * scalar);
    }

    @Override
    public Vector4 scl(Vector4 v) {
        return this.set(x * v.x, y * v.y, z * v.z, k * v.k);
    }

    @Override
    public float dst(Vector4 v) {
        final float a = v.x - x;
        final float b = v.y - y;
        final float c = v.z - z;
        final float d = v.k - k;

        return (float) Math.sqrt(a * a + b * b + c * c + d * d);
    }

    @Override
    public float dst2(Vector4 v) {
        final float a = v.x - x;
        final float b = v.y - y;
        final float c = v.z - z;
        final float d = v.k - k;

        return a * a + b * b + c * c + d * d;
    }

    @Override
    public Vector4 lerp(Vector4 target, float alpha) {
        x += alpha * (target.x - x);
        y += alpha * (target.y - y);
        z += alpha * (target.z - z);
        k += alpha * (target.k - k);

        return this;
    }

    @Override
    public Vector4 interpolate(Vector4 target, float alpha, Interpolation interpolator) {
        return lerp(target, interpolator.apply(0f, 1f, alpha));
    }

    @Override
    public Vector4 setToRandomDirection() {
        return new Vector4(0, 0, 0, 0);
    }

    @Override
    public boolean isUnit() {
        return isUnit(0.000000001f);
    }

    @Override
    public boolean isUnit(float margin) {
        return Math.abs(len2() - 1f) < margin;
    }

    @Override
    public boolean isZero() {
        return x == 0 && y == 0 && z == 0 && k == 0;
    }

    @Override
    public boolean isZero(final float margin) {
        return len2() < margin;
    }

    @Override
    public boolean isOnLine(Vector4 other, float epsilon) {
        return false;
    }

    @Override
    public boolean isOnLine(Vector4 other) {
        return false;
    }

    @Override
    public boolean isCollinear(Vector4 other, float epsilon) {
        return false;
    }

    @Override
    public boolean isCollinear(Vector4 other) {
        return false;
    }

    @Override
    public boolean isCollinearOpposite(Vector4 other, float epsilon) {
        return false;
    }

    @Override
    public boolean isCollinearOpposite(Vector4 other) {
        return false;
    }

    @Override
    public boolean isPerpendicular(Vector4 other) {
        return MathUtils.isZero(dot(other));
    }

    @Override
    public boolean isPerpendicular(Vector4 other, float epsilon) {
        return MathUtils.isZero(dot(other), epsilon);
    }

    @Override
    public boolean hasSameDirection(Vector4 other) {
        return dot(other) > 0;
    }

    @Override
    public boolean hasOppositeDirection(Vector4 other) {
        return dot(other) < 0;
    }

    @Override
    public boolean epsilonEquals(Vector4 other, float epsilon) {
        if (other == null) return false;
        if (Math.abs(other.x - x) > epsilon) return false;
        if (Math.abs(other.y - y) > epsilon) return false;
        if (Math.abs(other.z - z) > epsilon) return false;
        if (Math.abs(other.k - k) > epsilon) return false;

        return true;
    }

    @Override
    public Vector4 mulAdd(Vector4 vec, float scalar) {
        this.x += vec.x * scalar;
        this.y += vec.y * scalar;
        this.z += vec.z * scalar;
        this.k += vec.k * scalar;

        return this;
    }

    @Override
    public Vector4 mulAdd(Vector4 vec, Vector4 mulVec) {
        this.x += vec.x * mulVec.x;
        this.y += vec.y * mulVec.y;
        this.z += vec.z * mulVec.z;
        this.k += vec.k * mulVec.k;

        return this;
    }

    @Override
    public Vector4 setZero() {
        return set(0, 0, 0, 0);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + NumberUtils.floatToIntBits(x);
        result = prime * result + NumberUtils.floatToIntBits(y);
        result = prime * result + NumberUtils.floatToIntBits(z);
        result = prime * result + NumberUtils.floatToIntBits(k);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Vector4 other = (Vector4) obj;
        if (NumberUtils.floatToIntBits(x) != NumberUtils.floatToIntBits(other.x)) return false;
        if (NumberUtils.floatToIntBits(y) != NumberUtils.floatToIntBits(other.y)) return false;
        if (NumberUtils.floatToIntBits(z) != NumberUtils.floatToIntBits(other.z)) return false;
        if (NumberUtils.floatToIntBits(k) != NumberUtils.floatToIntBits(other.k)) return false;
        return true;
    }

    public Vector4 set(float x, float y, float z, float k) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.k = k;

        return this;
    }

    /**
     * Subtracts the other vector from this vector.
     *
     * @param x The x-component of the other vector
     * @param y The y-component of the other vector
     * @param z The z-component of the other vector
     * @return This vector for chaining
     */
    public Vector4 sub(float x, float y, float z, float k) {
        return this.set(this.x - x, this.y - y, this.z - z, this.k - k);
    }

    /**
     * Subtracts the given value from all components of this vector
     *
     * @param value The value
     * @return This vector for chaining
     */
    public Vector4 sub(float value) {
        return this.set(this.x - value, this.y - value, this.z - value, this.k - value);
    }

    /**
     * Adds the given vector to this component
     *
     * @param x The x-component of the other vector
     * @param y The y-component of the other vector
     * @param z The z-component of the other vector
     * @return This vector for chaining.
     */
    public Vector4 add(float x, float y, float z, float k) {
        return this.set(this.x + x, this.y + y, this.z + z, this.k + k);
    }

    /**
     * Adds the given value to all three components of the vector.
     *
     * @param values The value
     * @return This vector for chaining
     */
    public Vector4 add(float values) {
        return this.set(this.x + values, this.y + values, this.z + values, this.k + values);
    }

    /**
     * Scales this vector by the given values
     *
     * @param vx X value
     * @param vy Y value
     * @param vz Z value
     * @return This vector for chaining
     */
    public Vector4 scl(float vx, float vy, float vz, float kz) {
        return this.set(this.x * vx, this.y * vy, this.z * vz, this.k * kz);
    }
}
