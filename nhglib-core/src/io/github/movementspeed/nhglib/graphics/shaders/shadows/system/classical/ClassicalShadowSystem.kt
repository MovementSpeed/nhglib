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

package io.github.movementspeed.nhglib.graphics.shaders.shadows.system.classical

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import io.github.movementspeed.nhglib.core.ecs.systems.impl.RenderingSystem
import io.github.movementspeed.nhglib.graphics.shaders.shadows.system.BaseShadowSystem
import io.github.movementspeed.nhglib.graphics.shaders.shadows.system.FirstPassBaseShadowSystem
import io.github.movementspeed.nhglib.graphics.shaders.shadows.utils.DirectionalAnalyzer
import io.github.movementspeed.nhglib.graphics.shaders.shadows.utils.LightFilter
import io.github.movementspeed.nhglib.graphics.shaders.shadows.utils.NearFarAnalyzer
import io.github.movementspeed.nhglib.graphics.shaders.shadows.utils.ShadowMapAllocator

/** Classical shadow system uses shadow accumulation method. For each light, a depth map is generated and a second pass accumulate
 * the shadows. Obviously, the second pass must use the same lighting system as the main rendering pass. Compared to Realistic
 * shadow system, it's heavier but has some advantages:
 *
 * <pre>
 * 1 - It supports point light shadowing.
 * 2 - It's easy to use in custom shader.
 * 3 - There is no constraint about shader varying.
</pre> *
 * @author realitix
 */
class ClassicalShadowSystem(nearFarAnalyzer: NearFarAnalyzer, allocator: ShadowMapAllocator, directionalAnalyzer: DirectionalAnalyzer, lightFilter: LightFilter) : FirstPassBaseShadowSystem(nearFarAnalyzer, allocator, directionalAnalyzer, lightFilter) {

    /** true if it's the first light during second pass  */
    var isFirstCallPass2: Boolean = false
        protected set
    protected var nbCall = 0

    override val passQuantity: Int
        get() = PASS_QUANTITY

    val mainTexture: Texture
        get() = getTexture(SECOND_PASS)

    public override fun init(n: Int) {
        super.init(n)
        if (n == SECOND_PASS) init2()
    }

    override fun init1() {
        super.init1()
        passShaderProviders?.set(FIRST_PASS, Pass1ShaderProvider())
    }

    protected fun init2() {
        frameBuffers?.set(SECOND_PASS, FrameBuffer(Pixmap.Format.RGBA8888, RenderingSystem.renderWidth, RenderingSystem.renderHeight, true))
        passShaderProviders?.set(SECOND_PASS, Pass2ShaderProvider(Pass2Shader.Config(this)))
    }

    override fun beginPass(n: Int) {
        super.beginPass(n)
        if (n == SECOND_PASS) beginPass2()
    }

    protected fun beginPass2() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        isFirstCallPass2 = true
        nbCall = 0
    }

    override fun next(): Camera? {
        if (currentPass == SECOND_PASS && nbCall > 0) isFirstCallPass2 = false
        nbCall++
        return super.next()
    }

    override fun interceptCamera(lp: LightProperties): Camera? {
        return if (currentPass == SECOND_PASS) this.camera else lp.camera
    }

    override fun processViewport(lp: LightProperties, cameraViewport: Boolean) {
        if (this.currentPass != SECOND_PASS) super.processViewport(lp, cameraViewport)
    }

    override fun toString(): String {
        return "ClassicalShadowSystem"
    }

    companion object {
        const val PASS_QUANTITY = 2
        const val SECOND_PASS = 1
    }
}
