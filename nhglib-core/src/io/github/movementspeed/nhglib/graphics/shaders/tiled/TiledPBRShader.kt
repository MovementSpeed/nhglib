package io.github.movementspeed.nhglib.graphics.shaders.tiled

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.IntArray
import io.github.movementspeed.nhglib.Nhg
import io.github.movementspeed.nhglib.core.ecs.systems.impl.RenderingSystem
import io.github.movementspeed.nhglib.enums.LightType
import io.github.movementspeed.nhglib.enums.OpenGLVersion
import io.github.movementspeed.nhglib.graphics.lights.NhgLight
import io.github.movementspeed.nhglib.graphics.lights.NhgLightsAttribute
import io.github.movementspeed.nhglib.graphics.shaders.attributes.IBLAttribute
import io.github.movementspeed.nhglib.graphics.shaders.attributes.PBRTextureAttribute
import io.github.movementspeed.nhglib.graphics.shaders.attributes.ShadowSystemAttribute
import io.github.movementspeed.nhglib.graphics.shaders.shadows.system.classical.ClassicalShadowSystem
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils.hasAlbedo
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils.hasEmissive
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils.hasPbrNormal
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils.hasRMA
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils.useBones
import io.github.movementspeed.nhglib.utils.graphics.hasLights
import io.github.movementspeed.nhglib.utils.graphics.useGammaCorrection
import io.github.movementspeed.nhglib.utils.graphics.useImageBasedLighting

/**
 * Created by Fausto Napoli on 18/03/2017.
 */
class TiledPBRShader(private var renderable: Renderable?,
                     private val environment: Environment,
                     private val params: Params) : BaseShader() {
    private val gridSize = 10
    private var maxBonesLength = Integer.MIN_VALUE

    private val bonesIID: Int
    private var bonesLoc: Int = 0
    private var positionsAndRadiusesLoc: Int = 0
    private var directionsAndIntensitiesLoc: Int = 0

    private var vec1: Vector3? = null
    private var vec2: Vector2? = null
    private var idtMatrix: Matrix4? = null

    private var color: Color? = null

    private var lightPixmap: Pixmap? = null
    private var lightInfoPixmap: Pixmap? = null

    private var lightTexture: Texture? = null
    private var lightInfoTexture: Texture? = null

    private var shaderCamera: Camera? = null
    private var lightGrid: LightGrid? = null
    private val shaderProgram: ShaderProgram
    private var shadowSystem: ClassicalShadowSystem? = null

    private val lightTypes: IntArray
    private val lightAngles: FloatArray
    private val lightPositionsAndRadiuses: FloatArray
    private val lightDirectionsAndIntensities: FloatArray
    private var bones: FloatArray? = null

    private var lightsFrustum: Array<IntArray>? = null
    private var lights: Array<NhgLight>? = null

    init {
        val shadowSystemAttribute = environment.get(ShadowSystemAttribute.Type) as? ShadowSystemAttribute
        if (shadowSystemAttribute != null) {
            shadowSystem = shadowSystemAttribute.shadowSystem as ClassicalShadowSystem
        }

        val prefix = createPrefix(renderable!!)
        val folder = "shaders/"

        val vert = prefix + Gdx.files.internal(folder + "tf_pbr_shader.vert").readString()
        val frag = prefix + Gdx.files.internal(folder + "tf_pbr_shader.frag").readString()

        ShaderProgram.pedantic = false
        shaderProgram = ShaderProgram(vert, frag)

        val shaderLog = shaderProgram.log

        if (!shaderProgram.isCompiled) {
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

        register("u_cameraPosition", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, shaderCamera!!.position)
            }
        })

        register("u_graphicsWidth", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, Gdx.graphics.width)
            }
        })

        register("u_graphicsHeight", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, Gdx.graphics.height)
            }
        })

        register("u_albedoTiles", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                val textureAttribute = combinedAttributes.get(PBRTextureAttribute.Albedo) as? PBRTextureAttribute

                if (textureAttribute != null) {
                    shader.set(inputID, vec2!!.set(textureAttribute.tilesU, textureAttribute.tilesV))
                }
            }
        })

        register("u_normalTiles", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                val textureAttribute = combinedAttributes.get(PBRTextureAttribute.Normal) as? PBRTextureAttribute

                if (textureAttribute != null) {
                    shader.set(inputID, vec2!!.set(textureAttribute.tilesU, textureAttribute.tilesV))
                }
            }
        })

        register("u_rmaTiles", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                val textureAttribute = combinedAttributes.get(PBRTextureAttribute.RMA) as? PBRTextureAttribute

                if (textureAttribute != null) {
                    shader.set(inputID, vec2!!.set(textureAttribute.tilesU, textureAttribute.tilesV))
                }
            }
        })

        register("u_emissiveTiles", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                val textureAttribute = combinedAttributes.get(PBRTextureAttribute.Emissive) as? PBRTextureAttribute

                if (textureAttribute != null) {
                    shader.set(inputID, vec2!!.set(textureAttribute.tilesU, textureAttribute.tilesV))
                }
            }
        })

        val lightsAttribute = environment.get(NhgLightsAttribute.Type) as? NhgLightsAttribute
        lights = lightsAttribute?.lights ?: Array()

        val size = lights?.size ?: 0
        lightTypes = IntArray(size)
        lightAngles = FloatArray(size * 2)
        lightPositionsAndRadiuses = FloatArray(size * 4)
        lightDirectionsAndIntensities = FloatArray(size * 4)

        setLightTypes()
        setLightAngles()

        shaderProgram.begin()
        repeat(lightTypes.size) { i ->
            shaderProgram.setUniformi("u_lightTypes[$i]", lightTypes[i])
        }

        shaderProgram.setUniform2fv("u_lightAngles", lightAngles, 0, lights!!.size * 2)
        shaderProgram.end()

        bonesIID = register("u_bones", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                if (renderable.bones != null) {
                    val renderableBonesLength = renderable.bones.size * 16

                    if (renderableBonesLength > maxBonesLength) {
                        maxBonesLength = renderableBonesLength
                        bones = FloatArray(renderableBonesLength)
                    }

                    for (i in 0 until renderableBonesLength) {
                        val idx = i / 16
                        bones?.set(i, if (idx >= renderable.bones.size || renderable.bones[idx] == null)
                            idtMatrix!!.`val`[i % 16]
                        else
                            renderable.bones[idx].`val`[i % 16])
                    }

                    shaderProgram.setUniformMatrix4fv(bonesLoc, bones, 0, renderableBonesLength)
                }
            }
        })
    }

    override fun init() {
        super.init(shaderProgram, renderable)

        vec1 = Vector3()
        vec2 = Vector2()

        idtMatrix = Matrix4()
        bones = FloatArray(0)
        bonesLoc = loc(bonesIID)

        positionsAndRadiusesLoc = shaderProgram.fetchUniformLocation("u_lightPositionsAndRadiuses", false)
        directionsAndIntensitiesLoc = shaderProgram.fetchUniformLocation("u_lightDirectionsAndIntensities", false)

        color = Color()

        lightTexture = Texture(64, 128, Pixmap.Format.RGBA8888)
        lightTexture?.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)

        lightInfoTexture = Texture(1, 128, Pixmap.Format.RGBA8888)
        lightInfoTexture?.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)

        lightPixmap = Pixmap(64, 128, Pixmap.Format.RGBA8888)
        lightPixmap?.blending = Pixmap.Blending.None

        lightInfoPixmap = Pixmap(1, 128, Pixmap.Format.RGBA8888)
        lightInfoPixmap?.blending = Pixmap.Blending.None

        lightGrid = LightGrid(gridSize)
        lightsFrustum = Array()

        for (i in 0 until (lightGrid?.numTiles ?: 0)) {
            lightsFrustum?.add(IntArray())
        }
    }


    override fun compareTo(other: Shader): Int {
        return 0
    }

    override fun canRender(instance: Renderable): Boolean {
        val diffuse = hasAlbedo(instance) == params.albedo
        val rma = hasRMA(instance) == params.rma
        val normal = hasPbrNormal(instance) == params.normal
        val emissive = hasEmissive(instance) == params.emissive
        val bones = useBones(instance) == params.useBones
        val lit = hasLights(instance.environment) == params.lit
        val gammaCorrection = useGammaCorrection(instance.environment) == params.gammaCorrection
        val imageBasedLighting = useImageBasedLighting(instance.environment) == params.imageBasedLighting

        return diffuse && rma && normal && emissive && bones && lit && gammaCorrection && imageBasedLighting
    }

    override fun begin(camera: Camera, context: RenderContext) {
        this.shaderCamera = camera

        context.setCullFace(GL20.GL_BACK)
        context.setDepthTest(GL20.GL_LEQUAL)
        context.setDepthMask(true)

        Gdx.gl.glViewport(0, 0, RenderingSystem.renderWidth, RenderingSystem.renderHeight)

        lightGrid?.setFrustums(camera as PerspectiveCamera)

        makeLightTexture()
        setLightPositionsAndRadiusesAndDirections()

        super.begin(camera, context)

        lights?.size?.let { size ->
            shaderProgram.setUniform4fv(positionsAndRadiusesLoc, lightPositionsAndRadiuses, 0, size * 4)
            shaderProgram.setUniform4fv(directionsAndIntensitiesLoc, lightDirectionsAndIntensities, 0, size * 4)
        }
    }

    override fun render(renderable: Renderable, combinedAttributes: Attributes) {
        this.renderable = renderable

        if (!combinedAttributes.has(BlendingAttribute.Type)) {
            context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        }

        bindMaterial(combinedAttributes)
        bindTextures(combinedAttributes)

        super.render(renderable, combinedAttributes)
    }

    override fun dispose() {
        shaderProgram.dispose()
        super.dispose()
    }

    private fun bindMaterial(attributes: Attributes) {
        var depthFunc = GL20.GL_LEQUAL
        var depthRangeNear = 0f
        var depthRangeFar = 1f
        var depthMask = true

        attributes.forEach { attr ->
            val t = attr.type

            if (BlendingAttribute.`is`(t)) {
                context.setBlending(true, (attr as BlendingAttribute).sourceFunction, attr.destFunction)
            } else if (t and DepthTestAttribute.Type == DepthTestAttribute.Type) {
                val dta = attr as DepthTestAttribute
                depthFunc = dta.depthFunc
                depthRangeNear = dta.depthRangeNear
                depthRangeFar = dta.depthRangeFar
                depthMask = dta.depthMask
            }
        }

        context.setDepthTest(depthFunc, depthRangeNear, depthRangeFar)
        context.setDepthMask(depthMask)
    }

    private fun bindTextures(combinedAttributes: Attributes) {
        var bindValue: Int
        var texture: GLTexture
        context.textureBinder.begin()

        // Albedo
        var attribute: PBRTextureAttribute? = combinedAttributes.get(PBRTextureAttribute.Albedo) as? PBRTextureAttribute

        if (attribute != null) {
            texture = attribute.textureDescription.texture
            bindValue = context.textureBinder.bind(texture)
            shaderProgram.setUniformi("u_albedo", bindValue)
        }

        // RMA
        attribute = combinedAttributes.get(PBRTextureAttribute.RMA) as? PBRTextureAttribute

        if (attribute != null) {
            texture = attribute.textureDescription.texture
            bindValue = context.textureBinder.bind(texture)
            shaderProgram.setUniformi("u_rma", bindValue)
        }

        // Normal
        attribute = combinedAttributes.get(PBRTextureAttribute.Normal) as? PBRTextureAttribute

        if (attribute != null) {
            texture = attribute.textureDescription.texture
            bindValue = context.textureBinder.bind(texture)
            shaderProgram.setUniformi("u_normal", bindValue)
        }

        // Emissive
        attribute = combinedAttributes.get(PBRTextureAttribute.Emissive) as? PBRTextureAttribute

        if (attribute != null) {
            texture = attribute.textureDescription.texture
            bindValue = context.textureBinder.bind(texture)
            shaderProgram.setUniformi("u_emissive", bindValue)
        }

        // Lights
        bindValue = context.textureBinder.bind(lightTexture)
        shaderProgram.setUniformi("u_lights", bindValue)

        // Lights info
        bindValue = context.textureBinder.bind(lightInfoTexture)
        shaderProgram.setUniformi("u_lightInfo", bindValue)

        // Shadows
        if (shadowSystem != null) {
            bindValue = context.textureBinder.bind(shadowSystem!!.mainTexture)
            shaderProgram.setUniformi("u_shadowTexture", bindValue)
            shaderProgram.setUniformf("u_resolution",
                    RenderingSystem.renderWidth.toFloat(),
                    RenderingSystem.renderHeight.toFloat())
        }

        // Irradiance
        var iblAttribute: IBLAttribute? = combinedAttributes.get(IBLAttribute.IrradianceType) as? IBLAttribute

        if (iblAttribute != null) {
            texture = iblAttribute.textureDescription.texture
            bindValue = context.textureBinder.bind(texture)
            shaderProgram.setUniformi("u_irradiance", bindValue)
        }

        // Prefilter
        iblAttribute = combinedAttributes.get(IBLAttribute.PrefilterType) as? IBLAttribute

        if (iblAttribute != null) {
            texture = iblAttribute.textureDescription.texture
            bindValue = context.textureBinder.bind(texture)
            shaderProgram.setUniformi("u_prefilter", bindValue)
        }

        // Brdf
        iblAttribute = combinedAttributes.get(IBLAttribute.BrdfType) as? IBLAttribute

        if (iblAttribute != null) {
            texture = iblAttribute.textureDescription.texture
            bindValue = context.textureBinder.bind(texture)
            shaderProgram.setUniformi("u_brdf", bindValue)
        }

        context.textureBinder.end()
    }

    private fun makeLightTexture() {
        /* Calculates what lights are affecting what tiles.
         * This is done by dividing the camera frustum. size + size planes,
         * size rows and size columns to serve as the limits for size x size
         * small frustums (Meaning screen is divided into a size x size grid)
         */
        for (i in 0 until (lightGrid?.numTiles ?: 0)) {
            lightsFrustum?.get(i)?.clear()
        }

        lights?.forEachIndexed { index, l ->
            if (l.type != LightType.DIRECTIONAL_LIGHT) {
                lightsFrustum?.let {
                    lightGrid?.checkFrustums(l.position, l.radius, it, index)
                }
            } else {
                for (j in 0 until (lightGrid?.numTiles ?: 0)) {
                    lightsFrustum?.get(j)?.add(index)
                }
            }

            color?.set(l.color.r, l.color.g, l.color.b, 1.0f)
            lightInfoPixmap?.setColor(color!!)
            lightInfoPixmap?.drawPixel(0, index)
        }

        /* Creates a texture containing the color and radius
         * information about all light sources. Position could
         * be added here, but for this example it is not due to
         * limitations in precision.
         */
        lightInfoTexture?.draw(lightInfoPixmap, 0, 0)

        /* Creates a texture that contains a list of
         * light sources that are affecting each specific
         * tile. The row in the texture is decided by:
         * yTile*10+xTile and the following pixels on that
         * row are used to represent the ID of the light
         * sources.
         */
        for (row in 0 until (lightGrid?.numTiles ?: 0)) {
            var col = 0
            val r = lightsFrustum?.get(row)?.size?.toFloat() ?: 0f

            color?.set(r / 255, 0f, 0f, 0f)
            lightPixmap?.setColor(color)
            lightPixmap?.drawPixel(col, row)

            col++

            lightsFrustum?.forEachIndexed { index, j ->
                color?.set(j[index].toFloat() / 255, 0f, 0f, 0f)
                lightPixmap?.setColor(color)
                lightPixmap?.drawPixel(col, row)
                col++
            }
        }

        lightTexture?.draw(lightPixmap, 0, 0)
    }

    private fun setLightTypes() {
        lights?.forEachIndexed { index, light ->
            lightTypes[index] = light.type.ordinal
        }
    }

    private fun setLightAngles() {
        var i = 0

        lights?.forEach { light ->
            lightAngles[i++] = light.innerAngle
            lightAngles[i++] = light.outerAngle
        }
    }

    private fun setLightPositionsAndRadiusesAndDirections() {
        var i1 = 0
        var i2 = 0

        lights?.forEach { light ->
            vec1?.let {
                it.set(light.position)?.mul(shaderCamera?.view)

                lightPositionsAndRadiuses[i1++] = it.x
                lightPositionsAndRadiuses[i1++] = it.y
                lightPositionsAndRadiuses[i1++] = it.z
                lightPositionsAndRadiuses[i1++] = light.radius

                it.set(light.direction).rot(shaderCamera?.view)

                lightDirectionsAndIntensities[i2++] = it.x
                lightDirectionsAndIntensities[i2++] = it.y
                lightDirectionsAndIntensities[i2++] = it.z
                lightDirectionsAndIntensities[i2++] = light.intensity
            }
        }
    }

    private fun createPrefix(renderable: Renderable): String {
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

        if (params.useBones) {
            prefix += "#define numBones 12\n"

            renderable.meshPart.mesh.vertexAttributes?.forEach {
                if (it.usage == VertexAttributes.Usage.BoneWeight) {
                    prefix += "#define boneWeight" + it.unit + "Flag\n"
                }
            }
        }

        if (params.albedo) {
            prefix += "#define defAlbedo\n"
        }

        if (params.rma) {
            prefix += "#define defRMA\n"
        }

        if (params.normal) {
            prefix += "#define defNormal\n"
        }

        if (params.emissive) {
            prefix += "#define defEmissive\n"
        }

        if (params.gammaCorrection) {
            prefix += "#define defGammaCorrection\n"
        }

        if (params.imageBasedLighting) {
            prefix += "#define defImageBasedLighting\n"
        }

        if (params.lit) {
            val lightsAttribute = environment.get(NhgLightsAttribute.Type) as NhgLightsAttribute
            prefix += "#define lights " + lightsAttribute.lights.size + "\n"
        }

        val renderer = Gdx.gl.glGetString(GL30.GL_RENDERER).toUpperCase()

        if (renderer.contains("MALI")) {
            prefix += "#define GPU_MALI\n"
        } else if (renderer.contains("ADRENO")) {
            prefix += "#define GPU_ADRENO\n"
        }

        prefix += "#define GRID_SIZE $gridSize\n"

        return prefix
    }

    class Params {
        internal var useBones: Boolean = false
        internal var albedo: Boolean = false
        internal var normal: Boolean = false
        internal var rma: Boolean = false
        internal var emissive: Boolean = false
        internal var lit: Boolean = false
        internal var gammaCorrection: Boolean = false
        internal var imageBasedLighting: Boolean = false
    }
}