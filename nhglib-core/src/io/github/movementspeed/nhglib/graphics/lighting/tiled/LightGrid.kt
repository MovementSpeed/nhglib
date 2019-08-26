package io.github.movementspeed.nhglib.graphics.lighting.tiled

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.IntArray
import io.github.movementspeed.nhglib.graphics.lights.NhgLight
import io.github.movementspeed.nhglib.math.Vector4

class LightGrid {

    private var lightListLength: Int = 0

    val offsets = IntArray(TILES_COUNT)
    val counts = IntArray(TILES_COUNT)

    private val resolution = Vector2(RES_X.toFloat(), RES_Y.toFloat())
    private val tileSize = Vector2(TILE_SIZE_XY.toFloat(), TILE_SIZE_XY.toFloat())

    val lightList = IntArray()
    private val quads = Array<BoundingBox>()
    val viewSpaceLights = Array<NhgLight>()
    private val affectedTiles = Array<TileArea>()
    private var gridMinMax: Array<MinMax>? = null

    fun buildLightGrid(minMax: Array<MinMax>, lights: Array<NhgLight>, n: Float, view: Matrix4, projection: Matrix4) {
        //store minimum/maximum depth to lightgrid
        gridMinMax = minMax

        //compute ss bbs (bounding quads)
        computeBoundingQuads(lights, view, projection, n)

        //initialize light lists
        for (i in offsets.indices) {
            offsets[i] = 0
            counts[i] = 0
        }

        lightListLength = 0

        //Find light count for each tile
        for (i in 0 until quads.size) {
            val l = viewSpaceLights.get(i)

            var x = affectedTiles.get(i).x!!.x.toInt()
            while (x < affectedTiles.get(i).x!!.y - 1) {
                var y = affectedTiles.get(i).y!!.x.toInt()
                while (y < affectedTiles.get(i).y!!.y - 1) {
                    //tests light against the minimum/maximum of depth buffer
                    if (gridMinMax!!.size == 0 || gridMinMax!!.get(y * gridSize.x.toInt() + x).max < l.position.z + l.radius && gridMinMax!!.get(y * gridSize.x.toInt() + x).min > l.position.z - l.radius) {
                        lightListLength++
                        addToCounts(x, y, 1)
                    }
                    y++
                }
                x++
            }
        }

        //set offsets
        var offset = 0

        for (y in 0 until LIGHT_GRID_DIM_Y) {
            for (x in 0 until LIGHT_GRID_DIM_X) {
                val count = counts(x, y)

                setToOffsets(x, y, offset + count)
                offset += count
            }
        }

        lightList.setSize(lightListLength)

        if (lightList.size != 0) {
            val data = lightList

            for (i in 0 until quads.size) {

                val l = viewSpaceLights.get(i)

                var x = affectedTiles.get(i).x!!.x.toInt()
                while (x < affectedTiles.get(i).x!!.y - 1) {
                    var y = affectedTiles.get(i).y!!.x.toInt()
                    while (y < affectedTiles.get(i).y!!.y - 1) {
                        //tests light against the minimum/maximum of depth buffer
                        if (gridMinMax!!.size == 0 || gridMinMax!!.get(y * gridSize.x.toInt() + x).max < l.position.z + l.radius && gridMinMax!!.get(y * gridSize.x.toInt() + x).min > l.position.z - l.radius) {

                            // store reversely into next free slot
                            offset = offsets(x, y) - 1
                            data.set(offset, i)
                            setToOffsets(x, y, offset)
                        }
                        y++
                    }
                    x++
                }
            }
        }
    }

    fun getLightListLength(): Int {
        return lightList.size
    }

    private fun computeBoundingQuads(lights: Array<NhgLight>, view: Matrix4, projection: Matrix4, n: Float) {
        //clear vectors
        quads.clear()
        viewSpaceLights.clear()
        affectedTiles.clear()

        for (i in 0 until lights.size) {
            val l = lights.get(i).copy()

            //transform world light position to view space
            //Vector3 posVS = glm::vec3(view * glm::vec4(l.position, 1.0));
            val posVS = Vector3(l.position)
            posVS.mul(view)

            //compute bounding quad in clip space
            val clip = computeBoundingQuad(posVS, l.radius, n, projection)

            //transform quad to viewport
            clip.scl(-1f)

            val clipZ = clip.z
            clip.z = clip.x
            clip.x = clipZ

            val clipK = clip.k
            clip.k = clip.y
            clip.y = clipK

            //convert to the [0.0, 1.0] range
            clip.scl(0.5f).add(0.5f)

            //convert clip region to viewport
            val quad = BoundingBox()
            quad.min.x = clip.x * resolution.x
            quad.min.y = clip.y * resolution.y
            quad.max.x = clip.z * resolution.x
            quad.max.y = clip.k * resolution.y

            //store viewspace lights and their quads
            //lights are stored in world space
            if (quad.min.x < quad.max.x && quad.min.y < quad.max.y) {
                //store ss quad
                quads.add(quad)

                //convert light to view space and store it as viewspace light
                l.position.set(posVS)
                viewSpaceLights.add(l)

                computeLightAffectedTiles(quad.min.x, quad.max.x, quad.min.y, quad.max.y)
            }
        }
    }

    private fun computeLightAffectedTiles(minx: Float, maxx: Float, miny: Float, maxy: Float) {
        val x = Vector2(minx / TILE_SIZE_XY, (maxx + TILE_SIZE_XY - 1) / TILE_SIZE_XY)
        val y = Vector2(miny / TILE_SIZE_XY, (maxy + TILE_SIZE_XY - 1) / TILE_SIZE_XY)

        val tmp = TileArea()

        tmp.x = clamp(x, Vector2(), Vector2(gridSize.x + 1, gridSize.x + 1))
        tmp.y = clamp(y, Vector2(), Vector2(gridSize.y + 1, gridSize.y + 1))

        affectedTiles.add(tmp)
    }

    private fun clamp(value: Vector2, min: Vector2, max: Vector2): Vector2 {
        value.x = MathUtils.clamp(value.x, min.x, max.x)
        value.y = MathUtils.clamp(value.y, min.y, max.y)
        return value
    }

    private fun computeBoundingQuad(Lp: Vector3, Lr: Float, n: Float, projectionMatrix: Matrix4): Vector4 {
        var boundingQuad = Vector4(1.0f, 1.0f, -1.0f, -1.0f)

        if (Lp.z - Lr <= -n) {
            boundingQuad = Vector4(-1.0f, -1.0f, 1.0f, 1.0f)

            val minMax = MinMax()

            minMax.min = boundingQuad.x
            minMax.max = boundingQuad.z
            computeRoots(Lp.x, Lp.z, Lr, projectionMatrix.`val`[Matrix4.M00], minMax)
            boundingQuad.x = minMax.min
            boundingQuad.z = minMax.max

            minMax.min = boundingQuad.y
            minMax.max = boundingQuad.k
            computeRoots(Lp.y, Lp.z, Lr, projectionMatrix.`val`[Matrix4.M11], minMax)
            boundingQuad.y = minMax.min
            boundingQuad.k = minMax.max
        }

        return boundingQuad
    }

    internal fun computeRoots(Lc: Float, Lz: Float, Lr: Float, proj: Float, minMax: MinMax) {
        val LrSquare = Lr * Lr
        val LcSquare = Lc * Lc
        val LzSquare = Lz * Lz

        val denominator = LcSquare + LzSquare

        //eq (4.8)
        val D = LrSquare * LcSquare - denominator * (LrSquare - LzSquare)

        //check if point light does not fill whole screen
        if (D < 0.0) {
            return
        } else {
            val Nx1 = (Lc * Lr + Math.sqrt(D.toDouble()).toFloat()) / denominator
            val Nx2 = (Lc * Lr - Math.sqrt(D.toDouble()).toFloat()) / denominator

            updateRoots(Nx1, Lc, Lz, Lr, proj, minMax)
            updateRoots(Nx2, Lc, Lz, Lr, proj, minMax)
        }
    }

    private fun updateRoots(Nc: Float, Lc: Float, Lz: Float, Lr: Float, proj: Float, minMax: MinMax) {
        val Nz = (Lr - Nc * Lc) / Lz
        val Pz = (Lc * Lc + Lz * Lz - Lr * Lr) / (Lz - Nz / Nc * Lc)

        //check if point P lies in front of camera (z coords must be less than 0)
        if (Pz < 0.0f) {
            val c = -Nz * proj / Nc
            if (Nc < 0.0f) {
                minMax.min = Math.max(minMax.min, c)
            } else {
                minMax.max = Math.max(minMax.max, c)
            }
        }
    }

    private fun addToCounts(i: Int, j: Int, n: Int) {
        counts[i + j * LIGHT_GRID_DIM_Y] += n
    }

    private fun setToOffsets(i: Int, j: Int, n: Int) {
        offsets[i + j * LIGHT_GRID_DIM_X] = n
    }

    private fun offsets(i: Int, j: Int): Int {
        return offsets[i + j * LIGHT_GRID_DIM_X]
    }

    private fun counts(i: Int, j: Int): Int {
        return counts[i + j * LIGHT_GRID_DIM_Y]
    }

    companion object {
        var RES_X = 1280
        var RES_Y = 720

        //quads
        var QUAD_WIDTH = (RES_X - RES_X / 4) / 4
        var QUAD_HEIGHT = RES_Y / 6
        var QUAD_POS = RES_X - (QUAD_WIDTH + RES_X / 20)

        //lights
        var MAX_LIGHTS = 1024
        var LIGHT_RADIUS_MIN = 100.0f
        var LIGHT_RADIUS_MAX = 400.0f

        //lightgrid constants
        var TILE_SIZE_XY = 32
        var LIGHT_GRID_DIM_X = (RES_X + TILE_SIZE_XY - 1) / TILE_SIZE_XY
        var LIGHT_GRID_DIM_Y = (RES_Y + TILE_SIZE_XY - 1) / TILE_SIZE_XY
        var TILES_COUNT = LIGHT_GRID_DIM_X * LIGHT_GRID_DIM_Y

        var gridSize = Vector2(LIGHT_GRID_DIM_X.toFloat(), LIGHT_GRID_DIM_Y.toFloat())
    }
}
