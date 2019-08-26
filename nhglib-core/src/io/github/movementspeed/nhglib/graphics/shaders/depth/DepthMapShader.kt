package io.github.movementspeed.nhglib.graphics.shaders.depth

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Attributes
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.GdxRuntimeException
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils

class DepthMapShader(private var renderable: Renderable?, private val params: Params) : BaseShader() {
    private var bones: FloatArray? = null
    private val tmp: Vector2
    private var idtMatrix: Matrix4? = null

    override fun end() {
        super.end()
    }

    init {

        tmp = Vector2()

        val prefix = createPrefix(renderable)

        val vert = prefix + Gdx.files.internal("shaders/depth_shader.vert").readString()
        val frag = prefix + Gdx.files.internal("shaders/depth_shader.frag").readString()

        ShaderProgram.pedantic = false
        program = ShaderProgram(vert, frag)

        val shaderLog = program.log

        if (!program.isCompiled) {
            throw GdxRuntimeException(shaderLog)
        }

        register("u_mvpMatrix", object : BaseShader.GlobalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, shader.camera.combined)
            }
        })

        register("u_viewMatrix", object : BaseShader.GlobalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, shader.camera.view)
            }
        })

        register("u_modelMatrix", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, renderable.worldTransform)
            }
        })

        register("u_cameraRange", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, tmp.set(shader.camera.near, shader.camera.far))
            }
        })
    }

    override fun begin(camera: Camera, context: RenderContext) {
        super.begin(camera, context)
        context.setDepthTest(GL20.GL_LEQUAL)
        context.setCullFace(GL20.GL_BACK)
    }

    override fun render(renderable: Renderable) {
        if (!renderable.material.has(BlendingAttribute.Type)) {
            context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        } else {
            context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        }

        updateBones(renderable)
        super.render(renderable)
    }

    override fun init() {
        val program = this.program
        this.program = null
        init(program, renderable)
        renderable = null

        idtMatrix = Matrix4()
        bones = FloatArray(0)
    }

    override fun compareTo(other: Shader): Int {
        return 0
    }

    override fun canRender(instance: Renderable): Boolean {
        return ShaderUtils.useBones(instance) == params.useBones
    }

    private fun updateBones(renderable: Renderable) {
        if (renderable.bones != null) {
            bones = FloatArray(renderable.bones.size * 16)

            for (i in bones!!.indices) {
                val idx = i / 16
                bones[i] = if (idx >= renderable.bones.size || renderable.bones[idx] == null)
                    idtMatrix!!.`val`[i % 16]
                else
                    renderable.bones[idx].`val`[i % 16]
            }

            program.setUniformMatrix4fv("u_bones", bones, 0, bones!!.size)
        }
    }

    private fun createPrefix(renderable: Renderable): String {
        var prefix = ""

        if (params.useBones) {
            prefix += "#define numBones " + 12 + "\n"
            val n = renderable.meshPart.mesh.vertexAttributes.size()

            for (i in 0 until n) {
                val attr = renderable.meshPart.mesh.vertexAttributes.get(i)

                if (attr.usage == VertexAttributes.Usage.BoneWeight) {
                    prefix += "#define boneWeight" + attr.unit + "Flag\n"
                }
            }
        }

        return prefix
    }

    class Params {
        internal var useBones: Boolean = false
    }
}