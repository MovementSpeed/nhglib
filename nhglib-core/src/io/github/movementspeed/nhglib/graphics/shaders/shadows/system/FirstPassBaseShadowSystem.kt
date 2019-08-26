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

package io.github.movementspeed.nhglib.graphics.shaders.shadows.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import io.github.movementspeed.nhglib.graphics.shaders.shadows.utils.DirectionalAnalyzer
import io.github.movementspeed.nhglib.graphics.shaders.shadows.utils.LightFilter
import io.github.movementspeed.nhglib.graphics.shaders.shadows.utils.NearFarAnalyzer
import io.github.movementspeed.nhglib.graphics.shaders.shadows.utils.ShadowMapAllocator

/** FirstPassBaseShadowSystem assumes that the first pass renders all depth map in one texture.
 * @author realitix
 */
abstract class FirstPassBaseShadowSystem : BaseShadowSystem {

    constructor() : super()

    constructor(nearFarAnalyzer: NearFarAnalyzer, allocator: ShadowMapAllocator,
                directionalAnalyzer: DirectionalAnalyzer, lightFilter: LightFilter) : super(nearFarAnalyzer, allocator, directionalAnalyzer, lightFilter)

    override fun init(n: Int) {
        if (n == FIRST_PASS) init1()
    }

    protected open fun init1() {
        frameBuffers?.set(FIRST_PASS, FrameBuffer(Pixmap.Format.RGBA8888, allocator.width, allocator.height, true))
    }

    override fun beginPass(n: Int) {
        super.beginPass(n)
        if (n == FIRST_PASS) beginPass1()
    }

    protected fun beginPass1() {
        allocator.begin()
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST)
    }

    override fun endPass(n: Int) {
        super.endPass(n)
        if (n == FIRST_PASS) endPass1()
    }

    protected fun endPass1() {
        allocator.end()
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST)
    }

    companion object {
        var FIRST_PASS = 0
    }
}
