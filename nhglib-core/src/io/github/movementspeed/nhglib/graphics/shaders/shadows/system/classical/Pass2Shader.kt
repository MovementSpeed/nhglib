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
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.Attributes
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import io.github.movementspeed.nhglib.Nhg
import io.github.movementspeed.nhglib.enums.LightType
import io.github.movementspeed.nhglib.enums.OpenGLVersion
import io.github.movementspeed.nhglib.graphics.lights.NhgLight

/**
 * This shader accumulates shadow with blending
 *
 * @author realitix
 */
class Pass2Shader(renderable: Renderable, config: Config, shaderProgram: ShaderProgram) : DefaultShader(renderable, config, shaderProgram) {
    protected var blend = BlendingAttribute(GL20.GL_ONE, GL20.GL_ONE)
    protected var depth = DepthTestAttribute(GL20.GL_LEQUAL)
    protected var lightType = -1

    class Config(var shadowSystem: ClassicalShadowSystem) : DefaultShader.Config()

    class Inputs : DefaultShader.Inputs() {
        companion object {
            val shadowMapProjViewTrans = Uniform("u_shadowMapProjViewTrans")
            val shadowTexture = Uniform("u_shadowTexture")
            val uvTransform = Uniform("u_uvTransform")
            val lightColor = Uniform("u_lightColor")
            val lightDirection = Uniform("u_lightDirection")
            val lightIntensity = Uniform("u_lightIntensity")
            val lightPosition = Uniform("u_lightPosition")
            val lightCutoffAngle = Uniform("u_lightCutoffAngle")
            val lightExponent = Uniform("u_lightExponent")
        }
    }

    class Setters : DefaultShader.Setters() {
        companion object {
            val shadowMapProjViewTrans: Setter = object : BaseShader.GlobalSetter() {
                override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                    shader.set(inputID, shadowSystem?.currentLightProperties?.camera?.combined)
                }
            }

            val shadowTexture: Setter = object : BaseShader.GlobalSetter() {
                override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                    shader.set(inputID, shadowSystem?.getTexture(0))
                }
            }

            val uvTransform: Setter = object : BaseShader.GlobalSetter() {
                override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                    val tr = shadowSystem?.currentLightProperties?.region
                    tr?.let {
                        shader.set(inputID, tr.u, tr.v, tr.u2 - tr.u, tr.v2 - tr.v)
                    }
                }
            }

            val lightColor: Setter = object : BaseShader.GlobalSetter() {
                override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                    val l = shadowSystem?.currentLight
                    val intensity = when (l?.type) {
                        LightType.SPOT_LIGHT, LightType.POINT_LIGHT -> l.intensity
                        else -> 1f
                    }

                    l?.let {
                        shader.set(inputID, l.color.r * intensity, l.color.g * intensity, l.color.b * intensity)
                    }
                }
            }

            val lightDirection: Setter = object : BaseShader.GlobalSetter() {
                override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                    val l = shadowSystem?.currentLight

                    when (l?.type) {
                        LightType.POINT_LIGHT -> shader.set(inputID, shadowSystem?.currentLightProperties?.camera?.direction)
                        LightType.SPOT_LIGHT, LightType.DIRECTIONAL_LIGHT -> shader.set(inputID, l.direction)
                    }
                }
            }

            val lightIntensity: Setter = object : BaseShader.GlobalSetter() {
                override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                    val l = shadowSystem?.currentLight
                    when (l?.type) {
                        LightType.POINT_LIGHT, LightType.SPOT_LIGHT -> shader.set(inputID, l.intensity)
                    }
                }
            }

            val lightPosition: Setter = object : BaseShader.GlobalSetter() {
                override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                    val l = shadowSystem?.currentLight
                    when (l?.type) {
                        LightType.SPOT_LIGHT, LightType.POINT_LIGHT -> shader.set(inputID, l.position)
                    }
                }
            }

            val lightCutoffAngle: Setter = object : BaseShader.GlobalSetter() {
                override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                    if (shadowSystem?.currentLight?.type != LightType.DIRECTIONAL_LIGHT) {
                        shader.set(inputID, (shadowSystem?.currentLightProperties?.camera as? PerspectiveCamera)?.fieldOfView ?: 0f)
                    }
                }
            }

            val lightExponent: Setter = object : BaseShader.GlobalSetter() {
                override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                    val l = shadowSystem?.currentLight
                    shader.set(inputID, 5)
                }
            }
        }
    }

    @JvmOverloads
    constructor(renderable: Renderable, config: Config, prefix: String = createPrefix(renderable, config), vertexShader: String = if (config.vertexShader != null) config.vertexShader else getDefaultVertexShader(),
                fragmentShader: String = if (config.fragmentShader != null) config.fragmentShader else getDefaultFragmentShader()) : this(renderable, config, ShaderProgram(prefix + vertexShader, prefix + fragmentShader)) {
    }

    init {
        shadowSystem = config.shadowSystem
        register(Inputs.shadowMapProjViewTrans, Setters.shadowMapProjViewTrans)
        register(Inputs.shadowTexture, Setters.shadowTexture)
        register(Inputs.uvTransform, Setters.uvTransform)
        register(Inputs.lightColor, Setters.lightColor)
        register(Inputs.lightDirection, Setters.lightDirection)
        register(Inputs.lightPosition, Setters.lightPosition)
        register(Inputs.lightIntensity, Setters.lightIntensity)
        register(Inputs.lightCutoffAngle, Setters.lightCutoffAngle)
        register(Inputs.lightExponent, Setters.lightExponent)
    }

    override fun render(renderable: Renderable, combinedAttributes: Attributes) {
        if (shadowSystem?.isFirstCallPass2 == true)
            combinedAttributes.remove(BlendingAttribute.Type)
        else
            combinedAttributes.set(blend)

        combinedAttributes.set(depth)

        super.render(renderable, combinedAttributes)
    }

    override fun canRender(renderable: Renderable): Boolean {
        var ok = super.canRender(renderable)
        val dir = shadowSystem?.currentLight?.type == LightType.DIRECTIONAL_LIGHT

        if (lightType == -1) {
            lightType = LIGHT_SPOT
            if (dir) lightType = LIGHT_DIR
        }

        if (dir && lightType != LIGHT_DIR) ok = false
        if (!dir && lightType != LIGHT_SPOT) ok = false
        return ok
    }

    companion object {
        protected var shadowSystem: ClassicalShadowSystem? = null
        private var defaultVertexShader: String? = null

        fun getDefaultVertexShader(): String {
            if (defaultVertexShader == null)
                defaultVertexShader = Gdx.files.internal("shaders/shadows/classical/pass2.vertex.glsl")
                        .readString()
            return defaultVertexShader ?: ""
        }

        private var defaultFragmentShader: String? = null

        fun getDefaultFragmentShader(): String {
            if (defaultFragmentShader == null)
                defaultFragmentShader = Gdx.files.internal("shaders/shadows/classical/pass2.fragment.glsl")
                        .readString()
            return defaultFragmentShader ?: ""
        }

        const val LIGHT_SPOT = 0
        const val LIGHT_DIR = 1

        fun createPrefix(renderable: Renderable, config: Config): String {
            var prefix = ""

            if (Gdx.graphics.isGL30Available) {
                when (Nhg.glVersion) {
                    OpenGLVersion.VERSION_2 -> prefix = "#define GLVERSION 2\n"

                    OpenGLVersion.VERSION_3 -> {
                        prefix = "#version 300 es\n"
                        prefix += "#define GLVERSION 3\n"
                    }
                }
            } else {
                prefix = "#define GLVERSION 2\n"
            }

            val renderer = Gdx.gl.glGetString(GL30.GL_RENDERER).toUpperCase()

            if (renderer.contains("MALI")) {
                prefix += "#define GPU_MALI\n"
            } else if (renderer.contains("ADRENO")) {
                prefix += "#define GPU_ADRENO\n"
            }

            prefix += DefaultShader.createPrefix(renderable, config)
            val dir = config.shadowSystem.currentLight?.type == LightType.DIRECTIONAL_LIGHT

            prefix += if (dir) "#define directionalLight\n"
            else "#define spotLight\n"

            return prefix
        }
    }

}
