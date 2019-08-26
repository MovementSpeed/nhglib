package io.github.movementspeed.nhglib.graphics.shaders.forward

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
import io.github.movementspeed.nhglib.Nhg
import io.github.movementspeed.nhglib.core.ecs.systems.impl.RenderingSystem
import io.github.movementspeed.nhglib.graphics.lights.NhgLight
import io.github.movementspeed.nhglib.graphics.lights.NhgLightsAttribute
import io.github.movementspeed.nhglib.graphics.shaders.attributes.AmbientLightingAttribute
import io.github.movementspeed.nhglib.graphics.shaders.attributes.IBLAttribute
import io.github.movementspeed.nhglib.graphics.shaders.attributes.PBRTextureAttribute
import io.github.movementspeed.nhglib.graphics.shaders.interfaces.ShaderParams
import io.github.movementspeed.nhglib.utils.data.Bundle
import io.github.movementspeed.nhglib.utils.data.Strings
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils

/**
 * Created by Fausto Napoli on 18/03/2017.
 */
class PBRShader(private val renderable: Renderable, private val environment: Environment, shaderParams: ShaderParams) : BaseShader() {
    private val u_dirLights0color = register(BaseShader.Uniform("u_dirLights[0].color"))
    private val u_dirLights0direction = register(BaseShader.Uniform("u_dirLights[0].direction"))
    private val u_dirLights0intensity = register(BaseShader.Uniform("u_dirLights[0].intensity"))
    private val u_dirLights1color = register(BaseShader.Uniform("u_dirLights[1].color"))

    private val u_pointLights0color = register(BaseShader.Uniform("u_pointLights[0].color"))
    private val u_pointLights0position = register(BaseShader.Uniform("u_pointLights[0].position"))
    private val u_pointLights0intensity = register(BaseShader.Uniform("u_pointLights[0].intensity"))
    private val u_pointLights0radius = register(BaseShader.Uniform("u_pointLights[0].radius"))
    private val u_pointLights1color = register(BaseShader.Uniform("u_pointLights[1].color"))

    private val u_spotLights0color = register(BaseShader.Uniform("u_spotLights[0].color"))
    private val u_spotLights0position = register(BaseShader.Uniform("u_spotLights[0].position"))
    private val u_spotLights0direction = register(BaseShader.Uniform("u_spotLights[0].direction"))
    private val u_spotLights0intensity = register(BaseShader.Uniform("u_spotLights[0].intensity"))
    private val u_spotLights0innerAngle = register(BaseShader.Uniform("u_spotLights[0].innerAngle"))
    private val u_spotLights0outerAngle = register(BaseShader.Uniform("u_spotLights[0].outerAngle"))
    private val u_spotLights1color = register(BaseShader.Uniform("u_spotLights[1].color"))

    private var lightsSet: Boolean = false

    private var dirLightsLoc: Int = 0
    private var dirLightsColorOffset: Int = 0
    private var dirLightsDirectionOffset: Int = 0
    private var dirLightsIntensityOffset: Int = 0
    private var dirLightsSize: Int = 0

    private var pointLightsLoc: Int = 0
    private var pointLightsColorOffset: Int = 0
    private var pointLightsPositionOffset: Int = 0
    private var pointLightsIntensityOffset: Int = 0
    private var pointLightsRadiusOffset: Int = 0
    private var pointLightsSize: Int = 0

    private var spotLightsLoc: Int = 0
    private var spotLightsColorOffset: Int = 0
    private var spotLightsPositionOffset: Int = 0
    private var spotLightsDirectionOffset: Int = 0
    private var spotLightsIntensityOffset: Int = 0
    private var spotLightsInnerAngleOffset: Int = 0
    private var spotLightsOuterAngleOffset: Int = 0
    private var spotLightsSize: Int = 0

    private var maxBonesLength = Integer.MIN_VALUE
    private val bonesIID: Int
    private var bonesLoc: Int = 0
    private var bones: FloatArray? = null

    private val vec2: Vector2
    private val vec3: Vector3
    private var idtMatrix: Matrix4? = null

    private val params: Params
    private val shaderProgram: ShaderProgram

    private var lightsAttribute: NhgLightsAttribute? = null

    private val directionalLights: Array<NhgLight>
    private val pointLights: Array<NhgLight>
    private val spotLights: Array<NhgLight>

    init {
        this.params = shaderParams as PBRShader.Params

        vec2 = Vector2()
        vec3 = Vector3()

        this.directionalLights = arrayOfNulls<NhgLight>(if (params.lit) 2 else 0)
        for (i in directionalLights.indices) {
            directionalLights[i] = NhgLight()
        }

        this.pointLights = arrayOfNulls<NhgLight>(if (params.lit) 5 else 0)
        for (i in pointLights.indices) {
            pointLights[i] = NhgLight()
        }

        this.spotLights = arrayOfNulls<NhgLight>(0)
        for (i in spotLights.indices) {
            spotLights[i] = NhgLight()
        }

        val prefix = createPrefix(renderable)
        val folder = "shaders/"

        val vert = prefix + Gdx.files.internal(folder + "tf_pbr_shader.vert").readString()
        val frag = prefix + Gdx.files.internal(folder + "pbr_shader.frag").readString()

        ShaderProgram.pedantic = true
        shaderProgram = ShaderProgram(vert, frag)

        val shaderLog = shaderProgram.log

        if (!shaderProgram.isCompiled) {
            throw GdxRuntimeException(shaderLog)
        }

        register("u_mvpMatrix", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, shader.camera.combined)
            }
        })

        register("u_viewMatrix", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, shader.camera.view)
            }
        })

        register("u_modelMatrix", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, renderable.worldTransform)
            }
        })

        register("u_graphicsWidth", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, RenderingSystem.renderWidth)
            }
        })

        register("u_graphicsHeight", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                shader.set(inputID, RenderingSystem.renderHeight)
            }
        })

        register("u_albedoTiles", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                val textureAttribute = combinedAttributes.get(PBRTextureAttribute.Albedo) as PBRTextureAttribute

                if (textureAttribute != null) {
                    shader.set(inputID, vec2.set(textureAttribute.tilesU, textureAttribute.tilesV))
                }
            }
        })

        register("u_rmaTiles", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                val textureAttribute = combinedAttributes.get(PBRTextureAttribute.RMA) as PBRTextureAttribute

                if (textureAttribute != null) {
                    shader.set(inputID, vec2.set(textureAttribute.tilesU, textureAttribute.tilesV))
                }
            }
        })

        register("u_normalTiles", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                val textureAttribute = combinedAttributes.get(PBRTextureAttribute.Normal) as PBRTextureAttribute

                if (textureAttribute != null) {
                    shader.set(inputID, vec2.set(textureAttribute.tilesU, textureAttribute.tilesV))
                }
            }
        })

        register("u_ambient", object : BaseShader.LocalSetter() {
            override fun set(shader: BaseShader, inputID: Int, renderable: Renderable, combinedAttributes: Attributes) {
                val ambientLightingAttribute = combinedAttributes.get(AmbientLightingAttribute.Type) as AmbientLightingAttribute

                if (ambientLightingAttribute != null) {
                    shader.set(inputID, ambientLightingAttribute.ambient)
                } else {
                    shader.set(inputID, 0.03f)
                }
            }
        })

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
                        bones[i] = if (idx >= renderable.bones.size || renderable.bones[idx] == null)
                            idtMatrix!!.`val`[i % 16]
                        else
                            renderable.bones[idx].`val`[i % 16]
                    }

                    shaderProgram.setUniformMatrix4fv(bonesLoc, bones, 0, renderableBonesLength)
                }
            }
        })
    }

    override fun init() {
        super.init(shaderProgram, renderable)

        idtMatrix = Matrix4()
        bones = FloatArray(0)
        bonesLoc = loc(bonesIID)

        dirLightsLoc = loc(u_dirLights0color)
        dirLightsColorOffset = loc(u_dirLights0color) - dirLightsLoc
        dirLightsDirectionOffset = loc(u_dirLights0direction) - dirLightsLoc
        dirLightsIntensityOffset = loc(u_dirLights0intensity) - dirLightsLoc
        dirLightsSize = loc(u_dirLights1color) - dirLightsLoc
        if (dirLightsSize < 0) dirLightsSize = 0

        pointLightsLoc = loc(u_pointLights0color)
        pointLightsColorOffset = loc(u_pointLights0color) - pointLightsLoc
        pointLightsPositionOffset = loc(u_pointLights0position) - pointLightsLoc
        pointLightsIntensityOffset = if (has(u_pointLights0intensity)) loc(u_pointLights0intensity) - pointLightsLoc else -1
        pointLightsRadiusOffset = if (has(u_pointLights0radius)) loc(u_pointLights0radius) - pointLightsLoc else -1
        pointLightsSize = loc(u_pointLights1color) - pointLightsLoc
        if (pointLightsSize < 0) pointLightsSize = 0

        spotLightsLoc = loc(u_spotLights0color)
        spotLightsColorOffset = loc(u_spotLights0color) - spotLightsLoc
        spotLightsPositionOffset = loc(u_spotLights0position) - spotLightsLoc
        spotLightsDirectionOffset = loc(u_spotLights0direction) - spotLightsLoc
        spotLightsIntensityOffset = if (has(u_spotLights0intensity)) loc(u_spotLights0intensity) - spotLightsLoc else -1
        spotLightsInnerAngleOffset = loc(u_spotLights0innerAngle) - spotLightsLoc
        spotLightsOuterAngleOffset = loc(u_spotLights0outerAngle) - spotLightsLoc
        spotLightsSize = loc(u_spotLights1color) - spotLightsLoc
        if (spotLightsSize < 0) spotLightsSize = 0
    }


    override fun compareTo(other: Shader): Int {
        return 0
    }

    override fun canRender(instance: Renderable): Boolean {
        val albedo = ShaderUtils.hasAlbedo(instance) == params.albedo
        val rma = ShaderUtils.hasRMA(instance) == params.rma
        val normal = ShaderUtils.hasPbrNormal(instance) == params.normal
        val bones = ShaderUtils.useBones(instance) == params.useBones
        var lit = ShaderUtils.hasLights(instance.environment) == params.lit
        val gammaCorrection = ShaderUtils.useGammaCorrection(instance.environment) == params.gammaCorrection
        var imageBasedLighting = ShaderUtils.useImageBasedLighting(instance.environment) == params.imageBasedLighting

        if (renderable.userData != null) {
            val bundle = renderable.userData as Bundle
            val forceUnlit = bundle.getBoolean(Strings.RenderingSettings.forceUnlitKey, false)

            if (forceUnlit) {
                lit = true
                imageBasedLighting = true
            }
        }

        return albedo && rma && normal && bones && lit && gammaCorrection && imageBasedLighting
    }

    override fun begin(camera: Camera, context: RenderContext) {
        this.camera = camera

        context.setCullFace(GL20.GL_BACK)
        context.setDepthTest(GL20.GL_LEQUAL)
        context.setDepthMask(true)

        super.begin(camera, context)

        for (dirLight in directionalLights) {
            dirLight.color.set(0f, 0f, 0f, 1f)
            dirLight.direction.set(0f, -1f, 0f)
        }

        for (pointLight in pointLights) {
            pointLight.color.set(0f, 0f, 0f, 1f)
            pointLight.position.set(0f, 0f, 0f)
            pointLight.intensity = 0f
        }

        for (spotLight in spotLights) {
            spotLight.color.set(0f, 0f, 0f, 1f)
            spotLight.position.set(0f, 0f, 0f)
            spotLight.direction.set(0f, -1f, 0f)
            spotLight.intensity = 0f
            spotLight.innerAngle = 0f
            spotLight.outerAngle = 0f
        }

        lightsSet = false
    }

    override fun render(renderable: Renderable, combinedAttributes: Attributes) {
        if (!combinedAttributes.has(BlendingAttribute.Type)) {
            context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        }

        bindMaterial(combinedAttributes)
        bindTextures(combinedAttributes)

        if (params.lit) {
            bindLights(renderable)
        }

        super.render(renderable, combinedAttributes)
    }

    override fun end() {
        super.end()
    }

    override fun dispose() {
        shaderProgram.dispose()
        super.dispose()
    }

    private fun bindTextures(combinedAttributes: Attributes) {
        var bindValue: Int
        var texture: GLTexture
        context.textureBinder.begin()

        // Albedo
        var attribute: PBRTextureAttribute? = combinedAttributes.get(PBRTextureAttribute.Albedo) as PBRTextureAttribute

        if (attribute != null) {
            texture = attribute.textureDescription.texture
            bindValue = context.textureBinder.bind(texture)
            shaderProgram.setUniformi("u_albedo", bindValue)
        }

        // RMA
        attribute = combinedAttributes.get(PBRTextureAttribute.RMA) as PBRTextureAttribute

        if (attribute != null) {
            texture = attribute.textureDescription.texture
            bindValue = context.textureBinder.bind(texture)
            shaderProgram.setUniformi("u_rma", bindValue)
        }

        // Normal
        attribute = combinedAttributes.get(PBRTextureAttribute.Normal) as PBRTextureAttribute

        if (attribute != null) {
            texture = attribute.textureDescription.texture
            bindValue = context.textureBinder.bind(texture)
            shaderProgram.setUniformi("u_normal", bindValue)
        }

        // Emissive
        attribute = combinedAttributes.get(PBRTextureAttribute.Emissive) as PBRTextureAttribute

        if (attribute != null) {
            texture = attribute.textureDescription.texture
            bindValue = context.textureBinder.bind(texture)
            shaderProgram.setUniformi("u_emissive", bindValue)
        }

        // Irradiance
        var iblAttribute: IBLAttribute? = combinedAttributes.get(IBLAttribute.IrradianceType) as IBLAttribute

        if (iblAttribute != null) {
            texture = iblAttribute.textureDescription.texture
            bindValue = context.textureBinder.bind(texture)
            shaderProgram.setUniformi("u_irradiance", bindValue)
        }

        // Prefilter
        iblAttribute = combinedAttributes.get(IBLAttribute.PrefilterType) as IBLAttribute

        if (iblAttribute != null) {
            texture = iblAttribute.textureDescription.texture
            bindValue = context.textureBinder.bind(texture)
            shaderProgram.setUniformi("u_prefilter", bindValue)
        }

        // Brdf
        iblAttribute = combinedAttributes.get(IBLAttribute.BrdfType) as IBLAttribute

        if (iblAttribute != null) {
            texture = iblAttribute.textureDescription.texture
            bindValue = context.textureBinder.bind(texture)
            shaderProgram.setUniformi("u_brdf", bindValue)
        }

        context.textureBinder.end()
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
            prefix += "#define numBones " + 12 + "\n"
            val n = renderable.meshPart.mesh.vertexAttributes.size()

            for (i in 0 until n) {
                val attr = renderable.meshPart.mesh.vertexAttributes.get(i)

                if (attr.usage == VertexAttributes.Usage.BoneWeight) {
                    prefix += "#define boneWeight" + attr.unit + "Flag\n"
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

        if (params.gammaCorrection) {
            prefix += "#define defGammaCorrection\n"
        }

        if (params.imageBasedLighting) {
            prefix += "#define defImageBasedLighting\n"
        }

        if (params.lit) {
            val lightsAttribute = environment.get(NhgLightsAttribute.Type) as NhgLightsAttribute
            var directionalLights = 0
            var pointLights = 0
            var spotLights = 0

            for (light in lightsAttribute.lights) {
                when (light.type) {
                    LightType.DIRECTIONAL_LIGHT -> directionalLights++

                    LightType.SPOT_LIGHT -> spotLights++

                    LightType.POINT_LIGHT -> pointLights++
                }
            }

            if (directionalLights > 0) {
                prefix += "#define numDirectionalLights $directionalLights\n"
            }

            if (pointLights > 0) {
                prefix += "#define numPointLights $pointLights\n"
            }

            if (spotLights > 0) {
                prefix += "#define numSpotLights $spotLights\n"
            }
        }

        val renderer = Gdx.gl.glGetString(GL30.GL_RENDERER).toUpperCase()

        if (renderer.contains("MALI")) {
            prefix += "#define GPU_MALI\n"
        } else if (renderer.contains("ADRENO")) {
            prefix += "#define GPU_ADRENO\n"
        }

        return prefix
    }

    protected fun bindMaterial(attributes: Attributes) {
        var depthFunc = GL20.GL_LEQUAL
        var depthRangeNear = 0f
        var depthRangeFar = 1f
        var depthMask = true

        for (attr in attributes) {
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

    private fun bindLights(renderable: Renderable) {
        lightsAttribute = renderable.environment.get(NhgLightsAttribute.Type) as NhgLightsAttribute
        val lights = lightsAttribute!!.lights

        val dirs = Array<NhgLight>()
        val points = Array<NhgLight>()
        val spots = Array<NhgLight>()

        for (light in lights) {
            when (light.type) {
                LightType.DIRECTIONAL_LIGHT -> dirs.add(light)

                LightType.SPOT_LIGHT -> spots.add(light)

                LightType.POINT_LIGHT -> points.add(light)
            }
        }

        if (dirLightsLoc >= 0) {
            for (i in directionalLights.indices) {
                if (i >= dirs.size) {
                    if (lightsSet &&
                            directionalLights[i].color.r == 0f &&
                            directionalLights[i].color.g == 0f &&
                            directionalLights[i].color.b == 0f) {
                        continue
                    }

                    directionalLights[i].color.set(0f, 0f, 0f, 1f)
                } else if (lightsSet && directionalLights[i] == dirs.get(i)) {
                    continue
                } else {
                    directionalLights[i].set(dirs.get(i))
                }

                val idx = dirLightsLoc + i * dirLightsSize

                program.setUniformf(idx + dirLightsColorOffset,
                        directionalLights[i].color.r,
                        directionalLights[i].color.g,
                        directionalLights[i].color.b)

                vec3.set(directionalLights[i].direction)
                        .rot(camera.view)

                program.setUniformf(idx + dirLightsDirectionOffset, vec3.x, vec3.y, vec3.z)

                if (dirLightsIntensityOffset >= 0) {
                    program.setUniformf(idx + dirLightsIntensityOffset, directionalLights[i].intensity)
                }

                if (dirLightsSize <= 0) break
            }
        }

        if (pointLightsLoc >= 0) {
            for (i in pointLights.indices) {
                if (i >= points.size) {
                    if (lightsSet && pointLights[i].intensity == 0f) {
                        continue
                    }

                    pointLights[i].intensity = 0f
                } else if (lightsSet && pointLights[i] == points.get(i)) {
                    continue
                } else {
                    pointLights[i].set(points.get(i))
                }

                val idx = pointLightsLoc + i * pointLightsSize

                program.setUniformf(idx + pointLightsColorOffset,
                        pointLights[i].color.r,
                        pointLights[i].color.g,
                        pointLights[i].color.b)

                vec3.set(pointLights[i].position)
                vec3.mul(camera.view)

                program.setUniformf(idx + pointLightsPositionOffset, vec3.x, vec3.y, vec3.z)

                if (pointLightsIntensityOffset >= 0) {
                    program.setUniformf(idx + pointLightsIntensityOffset, pointLights[i].intensity)
                }

                if (pointLightsRadiusOffset >= 0) {
                    program.setUniformf(idx + pointLightsRadiusOffset, pointLights[i].radius)
                }

                if (pointLightsSize <= 0) {
                    break
                }
            }
        }

        if (spotLightsLoc >= 0) {
            for (i in spotLights.indices) {
                if (i >= spots.size) {
                    if (lightsSet && spotLights[i].intensity == 0f) {
                        continue
                    }

                    spotLights[i].intensity = 0f
                } else if (lightsSet && spotLights[i] == spots.get(i)) {
                    continue
                } else {
                    spotLights[i].set(spots.get(i))
                }

                val idx = spotLightsLoc + i * spotLightsSize

                program.setUniformf(idx + spotLightsColorOffset,
                        spotLights[i].color.r,
                        spotLights[i].color.g,
                        spotLights[i].color.b)

                vec3.set(pointLights[i].position)
                vec3.mul(camera.view)

                program.setUniformf(idx + spotLightsPositionOffset, vec3)

                vec3.set(directionalLights[i].direction)
                        .rot(camera.view)

                program.setUniformf(idx + spotLightsDirectionOffset, vec3)
                program.setUniformf(idx + spotLightsInnerAngleOffset, spotLights[i].innerAngle)
                program.setUniformf(idx + spotLightsOuterAngleOffset, spotLights[i].outerAngle)

                if (spotLightsIntensityOffset >= 0) {
                    program.setUniformf(idx + spotLightsIntensityOffset, spotLights[i].intensity)
                }

                if (spotLightsSize <= 0) {
                    break
                }
            }
        }

        lightsSet = true
    }

    class Params : ShaderParams {
        internal var useBones: Boolean = false
        internal var albedo: Boolean = false
        internal var normal: Boolean = false
        internal var rma: Boolean = false
        internal var lit: Boolean = false
        internal var gammaCorrection: Boolean = false
        internal var imageBasedLighting: Boolean = false
    }
}
