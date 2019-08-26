package io.github.movementspeed.nhglib.utils.data

import com.badlogic.gdx.math.Vector3

/**
 * Created by Fausto Napoli on 02/01/2017.
 * Simple data structure to hold 3D bounds data.
 */
class Bounds(width: Float, height: Float, depth: Float) {
    private var halfWidth: Float = 0f
    private var halfHeight: Float = 0f
    private var halfDepth: Float = 0f

    var width: Float = 0f
        set(width) = if (width > 0f) {
            field = width
            this.halfWidth = width / 2f
        } else {
            field = 0f
            this.halfWidth = 0f
        }

    var height: Float = 0f
        set(height) = if (height > 0f) {
            field = height
            this.halfHeight = height / 2f
        } else {
            field = 0f
            this.halfHeight = 0f
        }

    var depth: Float = 0f
        set(depth) = if (depth > 0f) {
            field = depth
            this.halfDepth = depth / 2f
        } else {
            field = 0f
            this.halfDepth = 0f
        }

    init {
        this.width = width
        this.height = height
        this.depth = depth
    }

    fun inBounds(point: Vector3): Boolean {
        var res = true

        if (point.x > halfWidth || point.x < -halfWidth) {
            res = false
        } else if (point.y > halfHeight || point.y < -halfHeight) {
            res = false
        } else if (point.z > halfDepth || point.z < -halfDepth) {
            res = false
        }

        return res
    }

    fun boundsInfo(point: Vector3): Info {
        val info = Info()
        info.inBounds = inBounds(point)

        if (!info.inBounds) {
            if (point.x > halfWidth) {
                info.widthSide = 1
            } else if (point.x < -halfWidth) {
                info.widthSide = -1
            }

            if (point.y > halfHeight) {
                info.heightSide = 1
            } else if (point.y < -halfHeight) {
                info.heightSide = -1
            }

            if (point.z > halfDepth) {
                info.depthSide = 1
            } else if (point.z < -halfDepth) {
                info.depthSide = -1
            }
        }

        return info
    }

    inner class Info {
        var inBounds: Boolean = false
        var widthSide: Int = 0
        var heightSide: Int = 0
        var depthSide: Int = 0

        init {
            inBounds = false
            widthSide = 0
            heightSide = 0
            depthSide = 0
        }
    }
}
