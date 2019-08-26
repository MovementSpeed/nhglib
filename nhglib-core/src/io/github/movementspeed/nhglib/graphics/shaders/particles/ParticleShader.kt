package io.github.movementspeed.nhglib.graphics.shaders.particles

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.GdxRuntimeException
import io.github.movementspeed.nhglib.Nhg
import io.github.movementspeed.nhglib.core.ecs.systems.impl.RenderingSystem

/**
 * This is a custom shader to render the particles. Usually is not required, because the [DefaultShader] will be used
 * instead. This shader will be used when dealing with billboards using GPU mode or point sprites.
 *
 * @author inferno
 */
class ParticleShader(
        /**
         * The renderable used to create this shader, invalid after the call to init
         */
        private var renderable: Renderable?, protected val config: Config, shaderProgram: ShaderProgram) : BaseShader() {

    var renderingSystem: RenderingSystem? = null

    private val materialMask: Long
    private val vertexMask: Long
    private var currentMaterial: Material? = null
    private val tmp: Vector2

    var defaultCullFace: Int
        get() = if (config.defaultCullFace == -1) GL20.GL_BACK else config.defaultCullFace
        set(cullFace) {
            config.defaultCullFace = cullFace
        }

    var defaultDepthFunc: Int
        get() = if (config.defaultDepthFunc == -1) GL20.GL_LEQUAL else config.defaultDepthFunc
        set(depthFunc) {
            config.defaultDepthFunc = depthFunc
        }

    @JvmOverloads
    constructor(renderable: Renderable, config: Config = Config(), prefix: String = createPrefix(renderable, config), vertexShader: String? = if (config.vertexShader != null) config.vertexShader else getDefaultVertexShader(),
                fragmentShader: String? = if (config.fragmentShader != null) config.fragmentShader else getDefaultFragmentShader()) : this(renderable, config, ShaderProgram(prefix + vertexShader!!, prefix + fragmentShader!!)) {
    }

    init {
        this.program = shaderProgram

        tmp = Vector2()

        materialMask = renderable?.material?.mask?.or(optionalAttributes) ?: 0
        vertexMask = renderable?.meshPart?.mesh?.vertexAttributes?.mask ?: 0

        if (!config.ignoreUnimplemented && implementedFlags and materialMask != materialMask)
            throw GdxRuntimeException("Some attributes not implemented yet ($materialMask)")

        // Global uniforms
        //register(Inputs.screenWidth, Setters.screenWidth);
        register(Inputs.cameraRight, Setters.cameraRight)
        register(Inputs.cameraInvDirection, Setters.cameraInvDirection)
        register(DefaultShader.Inputs.cameraUp, Setters.cameraUp)
        register(DefaultShader.Inputs.cameraPosition, Setters.cameraPosition)

        // Object uniforms
        register(DefaultShader.Inputs.diffuseTexture, DefaultShader.Setters.diffuseTexture)

        register("u_softness", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, 0.6f)
            }
        })

        register("u_screen", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, tmp.set(
                        Gdx.graphics.backBufferWidth.toFloat(),
                        Gdx.graphics.backBufferHeight.toFloat()))
            }
        })

        register("u_cameraRange", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, tmp.set(shader.camera.near, shader.camera.far))
            }
        })

        register("u_depthTexture", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                if (renderingSystem != null && RenderingSystem.depthTexture != null) {
                    shader.set(inputID, RenderingSystem.depthTexture)
                }
            }
        })

        register("u_viewMatrix", object : BaseShader.GlobalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, shader.camera.view)
            }
        })

        register("u_projectionMatrix", object : BaseShader.GlobalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, shader.camera.projection)
            }
        })
    }

    override fun init() {
        val program = this.program
        this.program = null
        init(program, renderable)
        renderable = null
    }

    override fun begin(camera: Camera, context: RenderContext) {
        super.begin(camera, context)

        //context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        //context.setDepthTest(GL20.GL_LEQUAL);
        //context.setDepthMask(true);
    }

    override fun render(renderable: Renderable) {
        if (!renderable.material.has(BlendingAttribute.Type)) {
            context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        }

        bindMaterial(renderable)
        super.render(renderable)
    }

    override fun end() {
        currentMaterial = null
        super.end()

        context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun canRender(renderable: Renderable): Boolean {
        return materialMask == renderable.material.mask or optionalAttributes && vertexMask == renderable.meshPart.mesh.vertexAttributes.mask
    }

    override fun dispose() {
        program.dispose()
        super.dispose()
    }

    override fun compareTo(other: Shader?): Int {
        if (other == null) return -1
        return if (other === this) 0 else 0
// FIXME compare shaders on their impact on performance
    }

    override fun equals(other: Any?): Boolean {
        return if (other is ParticleShader) equals(other as ParticleShader?) else false
    }

    fun equals(obj: ParticleShader): Boolean {
        return obj === this
    }

    protected fun bindMaterial(renderable: Renderable) {
        if (currentMaterial === renderable.material) return

        val cullFace = if (config.defaultCullFace == -1)
            GL20.GL_BACK
        else
            config.defaultCullFace

        var depthFunc = if (config.defaultDepthFunc == -1)
            GL20.GL_LEQUAL
        else
            config.defaultDepthFunc

        var depthRangeNear = 0f
        var depthRangeFar = 1f
        var depthMask = true

        currentMaterial = renderable.material

        for (attr in currentMaterial!!) {
            val t = attr.type

            if (BlendingAttribute.`is`(t)) {
                context.setBlending(true, particleMode.sFactor, particleMode.dFactor)
            } else if (t and DepthTestAttribute.Type == DepthTestAttribute.Type) {
                val dta = attr as DepthTestAttribute
                depthFunc = dta.depthFunc
                depthRangeNear = dta.depthRangeNear
                depthRangeFar = dta.depthRangeFar
                depthMask = dta.depthMask
            } else if (!config.ignoreUnimplemented) {
                throw GdxRuntimeException("Unknown material attribute: $attr")
            }
        }

        context.setCullFace(cullFace)
        context.setDepthTest(depthFunc, depthRangeNear, depthRangeFar)
        context.setDepthMask(depthMask)
    }

    object Inputs {
        val cameraRight = BaseShader.Uniform("u_cameraRight")
        val cameraInvDirection = BaseShader.Uniform("u_cameraInvDirection")
    }

    object Setters {
        val cameraRight: BaseShader.Setter = object : BaseShader.Setter {
            override fun isGlobal(shader: BaseShader, inputID: Int): Boolean {
                return true
            }

            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, TMP_VECTOR3.set(shader.camera.direction).crs(shader.camera.up).nor())
            }
        }

        val cameraUp: BaseShader.Setter = object : BaseShader.Setter {
            override fun isGlobal(shader: BaseShader, inputID: Int): Boolean {
                return true
            }

            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, TMP_VECTOR3.set(shader.camera.up).nor())
            }
        }

        val cameraInvDirection: BaseShader.Setter = object : BaseShader.Setter {
            override fun isGlobal(shader: BaseShader, inputID: Int): Boolean {
                return true
            }

            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID,
                        TMP_VECTOR3.set(-shader.camera.direction.x, -shader.camera.direction.y, -shader.camera.direction.z).nor())
            }
        }
        val cameraPosition: BaseShader.Setter = object : BaseShader.Setter {
            override fun isGlobal(shader: BaseShader, inputID: Int): Boolean {
                return true
            }

            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, shader.camera.position)
            }
        }
    }

    class Config {
        var softParticles = ParticleShader.softParticles
        var ignoreUnimplemented = true

        /**
         * Set to 0 to disable culling
         */
        var defaultCullFace = -1
        /**
         * Set to 0 to disable depth test
         */
        var defaultDepthFunc = -1

        /**
         * The uber vertex shader to use, null to use the default vertex shader.
         */
        var vertexShader: String? = null
        /**
         * The uber fragment shader to use, null to use the default fragment shader.
         */
        var fragmentShader: String? = null

        var align = AlignMode.Screen
        var type = ParticleType.Point

        constructor() {}

        constructor(align: AlignMode, type: ParticleType) {
            this.align = align
            this.type = type
        }

        constructor(align: AlignMode) {
            this.align = align
        }

        constructor(type: ParticleType) {
            this.type = type
        }

        constructor(vertexShader: String, fragmentShader: String) {
            this.vertexShader = vertexShader
            this.fragmentShader = fragmentShader
        }
    }

    enum class ParticleType {
        Billboard, Point
    }

    enum class AlignMode {
        Screen, ViewPoint
    }

    enum class ParticleMode private constructor(var sFactor: Int, var dFactor: Int) {
        ADDITIVE(GL30.GL_ONE, GL30.GL_ONE),
        SOURCE_ALPHA(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    }

    companion object {

        /**
         * Material attributes which are not required but always supported.
         */
        private val optionalAttributes = IntAttribute.CullFace or DepthTestAttribute.Type
        private val TMP_VECTOR3 = Vector3()

        var softParticles = false
        var particleMode = ParticleMode.SOURCE_ALPHA

        protected var implementedFlags = BlendingAttribute.Type or TextureAttribute.Diffuse

        private var defaultVertexShader: String? = null
        private var defaultFragmentShader: String? = null

        fun getDefaultVertexShader(): String {
            if (defaultVertexShader == null)
                defaultVertexShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/particles/particles.vertex.glsl").readString()
            return defaultVertexShader
        }

        fun getDefaultFragmentShader(): String {
            if (defaultFragmentShader == null)
                defaultFragmentShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/particles/particles.fragment.glsl")
                        .readString()
            return defaultFragmentShader
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

            if (config.type == ParticleType.Billboard) {
                prefix += "#define billboard\n"
                if (config.align == AlignMode.Screen)
                    prefix += "#define screenFacing\n"
                else if (config.align == AlignMode.ViewPoint) prefix += "#define viewPointFacing\n"
            }

            if (config.softParticles) {
                prefix += "#define SOFT_PARTICLES\n"
            }

            return prefix
        }
    }
}