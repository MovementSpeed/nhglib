package io.github.movementspeed.nhglib.graphics.shaders.tiled

import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Frustum
import com.badlogic.gdx.math.Plane
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.IntArray

class LightGrid(val size: Int) {
    private val sizeY: Int
    val numTiles: Int

    private val nearBotLeft: Vector3
    private val nearBotRight: Vector3
    private val nearTopRight: Vector3
    private val nearTopLeft: Vector3
    private val farBotLeft: Vector3
    private val farBotRight: Vector3
    private val farTopRight: Vector3
    private val farTopLeft: Vector3

    private val ndw = Vector3()
    private val ndh = Vector3()
    private val fdw = Vector3()
    private val fdh = Vector3()
    private val temp = Vector3()
    private val tempNearBotLeft = Vector3()
    private val tempFarBotLeft = Vector3()

    private val planePoints: Array<Vector3>

    private val verticalPlanes: Array<Array<Plane>>
    private val horizontalPlanes: Array<Array<Plane>>

    init {
        numTiles = size * size
        planePoints = Array(8)

        for (i in 0..7) {
            planePoints[i] = Vector3()
        }
        this.sizeY = size

        verticalPlanes = Array(2)
        horizontalPlanes = Array(2)

        repeat(2) {
            verticalPlanes[it] = Array(size)
            horizontalPlanes[it] = Array(sizeY)
        }

        for (i in 0 until this.size) {
            for (j in 0..1) {
                verticalPlanes[j][i] = Plane(Vector3(), 0f)
                horizontalPlanes[j][i] = Plane(Vector3(), 0f)
            }
        }

        nearBotLeft = Vector3()
        nearBotRight = Vector3()
        nearTopRight = Vector3()
        nearTopLeft = Vector3()
        farBotLeft = Vector3()
        farBotRight = Vector3()
        farTopRight = Vector3()
        farTopLeft = Vector3()
    }

    /* Builds the planes used for the sub frustums.
     */
    fun setFrustums(cam: PerspectiveCamera) {
        val bigFrustum = cam.frustum

        nearBotLeft.set(bigFrustum.planePoints[0])
        nearBotRight.set(bigFrustum.planePoints[1])
        nearTopRight.set(bigFrustum.planePoints[2])
        nearTopLeft.set(bigFrustum.planePoints[3])
        farBotLeft.set(bigFrustum.planePoints[4])
        farBotRight.set(bigFrustum.planePoints[5])
        farTopRight.set(bigFrustum.planePoints[6])
        farTopLeft.set(bigFrustum.planePoints[7])

        ndw.set(nearBotRight).sub(nearBotLeft).scl(1f / size)
        ndh.set(nearTopLeft).sub(nearBotLeft).scl(1f / sizeY)
        fdw.set(farBotRight).sub(farBotLeft).scl(1f / size)
        fdh.set(farTopRight).sub(farBotRight).scl(1f / sizeY)

        for (x in 0 until size) {
            temp.set(ndw).scl(x.toFloat())
            tempNearBotLeft.set(nearBotLeft)
            tempNearBotLeft.add(temp)

            planePoints[0].set(tempNearBotLeft)
            planePoints[1].set(tempNearBotLeft).add(ndw)
            planePoints[2].set(tempNearBotLeft).add(ndw).add(ndh)
            planePoints[3].set(tempNearBotLeft).add(ndh)

            temp.set(fdw).scl(x.toFloat())
            tempFarBotLeft.set(farBotLeft)
            tempFarBotLeft.add(temp)

            planePoints[4].set(tempFarBotLeft)
            planePoints[5].set(tempFarBotLeft).add(fdw)
            planePoints[6].set(tempFarBotLeft).add(fdw).add(fdh)
            planePoints[7].set(tempFarBotLeft).add(fdh)
            verticalPlanes[0][x].set(planePoints[0], planePoints[4], planePoints[3])
            verticalPlanes[1][x].set(planePoints[5], planePoints[1], planePoints[6])
        }

        for (y in 0 until sizeY) {
            tempNearBotLeft.set(nearBotLeft)
            temp.set(ndh).scl(y.toFloat())
            tempNearBotLeft.set(tempNearBotLeft)
            tempNearBotLeft.add(temp)

            planePoints[0].set(tempNearBotLeft)
            planePoints[1].set(tempNearBotLeft).add(ndw)
            planePoints[2].set(tempNearBotLeft).add(ndw).add(ndh)
            planePoints[3].set(tempNearBotLeft).add(ndh)

            tempFarBotLeft.set(farBotLeft)

            temp.set(fdh).scl(y.toFloat())
            tempFarBotLeft.set(tempFarBotLeft)
            tempFarBotLeft.add(temp)
            planePoints[4].set(tempFarBotLeft)
            planePoints[5].set(tempFarBotLeft).add(fdw)
            planePoints[6].set(tempFarBotLeft).add(fdw).add(fdh)
            planePoints[7].set(tempFarBotLeft).add(fdh)
            horizontalPlanes[0][y].set(planePoints[2], planePoints[3], planePoints[6])
            horizontalPlanes[1][y].set(planePoints[4], planePoints[0], planePoints[1])
        }
    }

    /* Check what tiles a given light source intersects
     * and updates the array lights accordingly.
     */
    fun checkFrustums(pos: Vector3, radius: Float, lights: Array<IntArray>, lightID: Int) {
        var startX = 0
        var endX = 0
        var startY = 0
        var endY = 0

        var foundStart = false
        var foundEnd = false

        for (x in 0 until size) {
            if (insideColumn(x, pos, radius)) {
                if (!foundStart) {
                    startX = x
                }

                foundStart = true
            } else {
                if (foundStart) {
                    endX = x - 1
                    foundEnd = true
                    break
                }
            }
        }

        if (!foundEnd && foundStart) {
            endX = size - 1
        }

        foundStart = false
        foundEnd = false

        for (y in 0 until sizeY) {
            if (insideRow(y, pos, radius)) {
                if (!foundStart) {
                    startY = y
                }

                foundStart = true
            } else {
                if (foundStart) {
                    endY = y - 1
                    foundEnd = true
                    break
                }
            }
        }

        if (!foundEnd && foundStart) {
            endY = sizeY - 1
        }

        for (x in startX..endX) {
            for (y in startY..endY) {
                lights.get(y * size + x).add(lightID)
            }
        }
    }

    private fun insideColumn(x: Int, pos: Vector3, radius: Float): Boolean {
        return if (verticalPlanes[0][x].distance(pos) < -radius) false else verticalPlanes[1][x].distance(pos) >= -radius
    }

    private fun insideRow(y: Int, pos: Vector3, radius: Float): Boolean {
        return if (horizontalPlanes[0][y].distance(pos) < -radius) false else horizontalPlanes[1][y].distance(pos) >= -radius
    }
}
