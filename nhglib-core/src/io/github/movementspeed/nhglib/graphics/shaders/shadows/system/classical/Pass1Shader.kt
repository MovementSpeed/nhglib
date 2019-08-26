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
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import io.github.movementspeed.nhglib.Nhg
import io.github.movementspeed.nhglib.enums.LightType
import io.github.movementspeed.nhglib.enums.OpenGLVersion

/** This shader pack the depth data into the texture
 * @author realitix
 */
class Pass1Shader(renderable: Renderable, config: Config, shaderProgram: ShaderProgram) : DefaultShader(renderable, config, shaderProgram) {

    @JvmOverloads
    constructor(renderable: Renderable, config: Config = Config(), prefix: String = createPrefix(renderable, config), vertexShader: String = if (config.vertexShader != null) config.vertexShader else getDefaultVertexShader(),
                fragmentShader: String = if (config.fragmentShader != null) config.fragmentShader else getDefaultFragmentShader()) : this(renderable, config, ShaderProgram(prefix + vertexShader, prefix + fragmentShader))

    companion object {
        private var defaultVertexShader: String? = null

        fun getDefaultVertexShader(): String {
            if (defaultVertexShader == null)
                defaultVertexShader = Gdx.files.internal("shaders/shadows/classical/pass1.vertex.glsl")
                        .readString()
            return defaultVertexShader ?: ""
        }

        private var defaultFragmentShader: String? = null

        fun getDefaultFragmentShader(): String {
            if (defaultFragmentShader == null)
                defaultFragmentShader = Gdx.files.internal("shaders/shadows/classical/pass1.fragment.glsl")
                        .readString()
            return defaultFragmentShader ?: ""
        }

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
            return prefix
        }
    }
}
