/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.movementspeed.nhglib.graphics.shaders.shadows.utils

import com.badlogic.gdx.graphics.g3d.environment.BaseLight
import com.badlogic.gdx.utils.GdxRuntimeException
import kotlin.math.roundToInt
import kotlin.math.sqrt

/** FixedShadowMapAllocator behavior is naive. It separates the texture in several parts and for each light increments the region.
 * The larger the size, the better the quality or quantity.
 *
 *
 * Examples: <br></br>
 * If you set size to QUALITY_MAX and mapQuantity to NB_MAP_MIN, each depth map would be 2048*2048 (it's huge!).<br></br>
 * If you set size to QUALITY_MIN and mapQuantity to NB_MAP_MAX, each depth map would be 64*64.
 *
 * @author realitix
 */
class FixedShadowMapAllocator
/** Create new FixedShadowMapAllocator
 * @param size Size of shadow map
 * @param nbMap Quantity of supported regions
 */
(
        /** Shadow map size (Width = Height)  */
        override val width: Int,
        /** Quantity of renderable parts  */
        /** @return Quantity of supported regions.
         */
        val mapQuantity: Int) : ShadowMapAllocator {
    /** Current rendered part  */
    protected var currentMap: Int = 0
    /** Result region  */
    protected var result = ShadowMapAllocator.ShadowMapRegion()
    /** Is in allocation state  */
    protected var allocating = false

    override val height: Int
        get() = width

    override fun begin() {
        if (allocating) {
            throw GdxRuntimeException("Allocator must end before begin")
        }
        allocating = true
        currentMap = 0
    }

    override fun end() {
        if (!allocating) {
            throw GdxRuntimeException("Allocator must begin before end")
        }
        allocating = false
    }

    override fun nextResult(light: BaseLight<*>): ShadowMapAllocator.ShadowMapRegion? {
        if (!allocating) {
            throw GdxRuntimeException("Allocator must begin before call")
        }

        val nbOnLine = sqrt(mapQuantity.toDouble()).roundToInt()
        val i = currentMap % nbOnLine
        val j = currentMap / nbOnLine
        val sizeMap = width / nbOnLine

        result.x = i * sizeMap
        result.y = j * sizeMap
        result.width = sizeMap
        result.height = sizeMap

        if (result.x >= width || result.y >= width) return null

        currentMap += 1

        return result
    }

    companion object {
        /** Helpers to choose shadow map quality  */
        val QUALITY_MIN = 1024
        val QUALITY_MED = 2048
        val QUALITY_MAX = 4096

        /** Helpers to choose number of supported shadows  */
        val QUANTITY_MAP_MIN = 1
        val QUANTITY_MAP_LOW = 4
        val QUANTITY_MAP_MED = 16
        val QUANTITY_MAP_MAX = 32
    }
}
