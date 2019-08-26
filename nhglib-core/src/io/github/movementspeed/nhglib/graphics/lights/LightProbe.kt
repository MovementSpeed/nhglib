package io.github.movementspeed.nhglib.graphics.lights

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.FrameBufferCubemap
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.UBJsonReader
import io.github.movementspeed.nhglib.files.HDRData

/**
 * Created by Fausto Napoli on 17/08/2017.
 */
class LightProbe {
    private var environmentWidth: Float = 0.toFloat()
    private var environmentHeight: Float = 0.toFloat()

    private var irradianceWidth: Float = 0.toFloat()
    private var irradianceHeight: Float = 0.toFloat()

    private var prefilterWidth: Float = 0.toFloat()
    private var prefilterHeight: Float = 0.toFloat()

    private var brdfWidth: Float = 0.toFloat()
    private var brdfHeight: Float = 0.toFloat()

    var environment: Cubemap? = null
        private set
    var irradiance: Cubemap? = null
        private set
    var prefilter: Cubemap? = null
        private set

    var brdf: Texture? = null
        private set

    private var quadModel: Model? = null
    private var cubeModel: Model? = null

    private var quadMesh: Mesh? = null
    private var cubeMesh: Mesh? = null

    private var perspectiveCameras: Array<PerspectiveCamera>? = null

    init {
        init()
    }

    fun build(environment: Texture,
              environmentWidth: Float, environmentHeight: Float,
              irradianceWidth: Float, irradianceHeight: Float,
              prefilterWidth: Float, prefilterHeight: Float,
              brdfWidth: Float, brdfHeight: Float) {
        this.environmentWidth = environmentWidth
        this.environmentHeight = environmentHeight
        this.irradianceWidth = irradianceWidth
        this.irradianceHeight = irradianceHeight
        this.prefilterWidth = prefilterWidth
        this.prefilterHeight = prefilterHeight
        this.brdfWidth = brdfWidth
        this.brdfHeight = brdfHeight

        initCameras()

        this.environment = renderEnvironmentFromTexture(environment)
        irradiance = renderIrradiance(this.environment)
        prefilter = renderPrefilter(this.environment)
        brdf = renderBRDF()

        cubeModel!!.dispose()
        quadModel!!.dispose()
    }

    fun build(hdrData: HDRData?,
              environmentWidth: Float, environmentHeight: Float,
              irradianceWidth: Float, irradianceHeight: Float,
              prefilterWidth: Float, prefilterHeight: Float,
              brdfWidth: Float, brdfHeight: Float) {
        this.environmentWidth = environmentWidth
        this.environmentHeight = environmentHeight
        this.irradianceWidth = irradianceWidth
        this.irradianceHeight = irradianceHeight
        this.prefilterWidth = prefilterWidth
        this.prefilterHeight = prefilterHeight
        this.brdfWidth = brdfWidth
        this.brdfHeight = brdfHeight

        initCameras()

        if (hdrData != null) {
            environment = renderEnvironmentFromHDRData(hdrData)
        } else {
            environment = renderEnvironmentFromScene()
        }

        irradiance = renderIrradiance(environment)
        prefilter = renderPrefilter(environment)
        brdf = renderBRDF()

        cubeModel!!.dispose()
        quadModel!!.dispose()
    }

    fun build(environmentWidth: Float, environmentHeight: Float) {
        build(null as HDRData?, environmentWidth, environmentHeight, 32f, 32f,
                128f, 128f, environmentWidth, environmentHeight)
    }

    private fun init() {
        createMeshes()
    }

    private fun createMeshes() {
        val mb = ModelBuilder()
        cubeModel = mb.createBox(1f, 1f, 1f, Material(),
                (VertexAttributes.Usage.Position or
                        VertexAttributes.Usage.Normal or
                        VertexAttributes.Usage.TextureCoordinates).toLong())
        cubeMesh = cubeModel!!.meshes.first()

        val modelLoader = G3dModelLoader(UBJsonReader())
        quadModel = modelLoader.loadModel(Gdx.files.internal("models/quad.g3db"))
        quadMesh = quadModel!!.meshes.first()
    }

    private fun initCameras() {
        perspectiveCameras = Array()

        for (i in 0..5) {
            val pc = PerspectiveCamera(90f, environmentWidth, environmentHeight)
            pc.near = 0.1f
            pc.far = 10.0f
            perspectiveCameras!!.add(pc)
        }

        val pc1 = perspectiveCameras!!.get(0)
        pc1.lookAt(1f, 0f, 0f)
        pc1.rotate(Vector3.X, 180f)
        pc1.update()

        val pc2 = perspectiveCameras!!.get(1)
        pc2.lookAt(0f, 0f, -1f)
        pc2.rotate(Vector3.X, 180f)
        pc2.update()

        val pc3 = perspectiveCameras!!.get(2)
        pc3.lookAt(0f, 0f, 1f)
        pc3.rotate(Vector3.X, 180f)
        pc3.update()

        val pc4 = perspectiveCameras!!.get(3)
        pc4.lookAt(0f, 1f, 0f)
        pc4.rotate(Vector3.Y, 270f)
        pc4.update()

        val pc5 = perspectiveCameras!!.get(4)
        pc5.lookAt(0f, -1f, 0f)
        pc5.rotate(Vector3.Y, 270f)
        pc5.update()

        val pc6 = perspectiveCameras!!.get(5)
        pc6.lookAt(-1f, 0f, 0f)
        pc6.rotate(Vector3.X, 180f)
        pc6.update()
    }

    private fun renderEnvironmentFromHDRData(data: HDRData): Cubemap {
        val equirectangularTexture: Texture?
        val folder = "shaders/"

        val equiToCubeShader = ShaderProgram(
                Gdx.files.internal(folder + "equi_to_cube_shader.vert"),
                Gdx.files.internal(folder + "equi_to_cube_shader.frag"))

        val shaderLog = equiToCubeShader.log

        if (!equiToCubeShader.isCompiled) {
            throw GdxRuntimeException(shaderLog)
        }

        equirectangularTexture = data.texture

        val builder = GLFrameBuffer.FrameBufferCubemapBuilder(
                environmentWidth.toInt(), environmentHeight.toInt())
        builder.addColorTextureAttachment(GL30.GL_RGB8, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE)
        builder.addBasicDepthRenderBuffer()
        val frameBufferCubemap = builder.build()

        equirectangularTexture!!.bind(0)
        equiToCubeShader.begin()
        equiToCubeShader.setUniformMatrix("u_projection", perspectiveCameras!!.first().projection)
        equiToCubeShader.setUniformi("u_equirectangularMap", 0)
        frameBufferCubemap.begin()
        for (i in 0..5) {
            equiToCubeShader.setUniformMatrix("u_view", perspectiveCameras!!.get(i).view)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
            cubeMesh!!.render(equiToCubeShader, GL20.GL_TRIANGLES)
            frameBufferCubemap.nextSide()
        }
        frameBufferCubemap.end()
        equiToCubeShader.end()
        equiToCubeShader.dispose()

        return frameBufferCubemap.colorBufferTexture
    }

    private fun renderEnvironmentFromTexture(equirectangularTexture: Texture): Cubemap {
        val folder = "shaders/"

        val equiToCubeShader = ShaderProgram(
                Gdx.files.internal(folder + "equi_to_cube_shader.vert"),
                Gdx.files.internal(folder + "equi_to_cube_shader.frag"))

        val shaderLog = equiToCubeShader.log

        if (!equiToCubeShader.isCompiled) {
            throw GdxRuntimeException(shaderLog)
        }

        val builder = GLFrameBuffer.FrameBufferCubemapBuilder(
                environmentWidth.toInt(), environmentHeight.toInt())
        builder.addColorTextureAttachment(GL30.GL_RGB8, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE)
        builder.addBasicDepthRenderBuffer()
        val frameBufferCubemap = builder.build()

        equirectangularTexture.bind(0)
        equiToCubeShader.begin()
        equiToCubeShader.setUniformMatrix("u_projection", perspectiveCameras!!.first().projection)
        equiToCubeShader.setUniformi("u_equirectangularMap", 0)
        frameBufferCubemap.begin()
        for (i in 0..5) {
            equiToCubeShader.setUniformMatrix("u_view", perspectiveCameras!!.get(i).view)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
            cubeMesh!!.render(equiToCubeShader, GL20.GL_TRIANGLES)
            frameBufferCubemap.nextSide()
        }
        frameBufferCubemap.end()
        equiToCubeShader.end()
        equiToCubeShader.dispose()

        return frameBufferCubemap.colorBufferTexture
    }

    private fun renderEnvironmentFromScene(): Cubemap? {
        return null
    }

    private fun renderIrradiance(environmentCubemap: Cubemap?): Cubemap {
        val folder = "shaders/"

        val irradianceShader = ShaderProgram(
                Gdx.files.internal(folder + "equi_to_cube_shader.vert"),
                Gdx.files.internal(folder + "irradiance_shader.frag"))

        val shaderLog = irradianceShader.log

        if (!irradianceShader.isCompiled) {
            throw GdxRuntimeException(shaderLog)
        }

        var frameBufferCubemap: FrameBufferCubemap

        try {
            frameBufferCubemap = FrameBufferCubemap(Pixmap.Format.RGB888,
                    irradianceWidth.toInt(), irradianceHeight.toInt(), true)
        } catch (e: IllegalStateException) {
            frameBufferCubemap = FrameBufferCubemap(Pixmap.Format.RGB565,
                    irradianceWidth.toInt(), irradianceHeight.toInt(), true)
        }

        environmentCubemap!!.bind(0)
        irradianceShader.begin()
        irradianceShader.setUniformMatrix("u_projection", perspectiveCameras!!.first().projection)
        irradianceShader.setUniformi("u_environmentMap", 0)
        frameBufferCubemap.begin()
        for (i in 0..5) {
            irradianceShader.setUniformMatrix("u_view", perspectiveCameras!!.get(i).view)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
            cubeMesh!!.render(irradianceShader, GL20.GL_TRIANGLES)
            frameBufferCubemap.nextSide()
        }
        frameBufferCubemap.end()
        irradianceShader.end()
        irradianceShader.dispose()

        return frameBufferCubemap.colorBufferTexture
    }

    private fun renderPrefilter(environmentCubemap: Cubemap?): Cubemap {
        val folder = "shaders/"

        val prefilterShader = ShaderProgram(
                Gdx.files.internal(folder + "equi_to_cube_shader.vert"),
                Gdx.files.internal(folder + "prefilter_shader.frag"))

        val shaderLog = prefilterShader.log

        if (!prefilterShader.isCompiled) {
            throw GdxRuntimeException(shaderLog)
        }

        val perspectiveCameras = Array<PerspectiveCamera>()

        for (i in 0..5) {
            val pc = PerspectiveCamera(90f, prefilterWidth, prefilterHeight)
            pc.near = 0.1f
            pc.far = 10.0f
            perspectiveCameras.add(pc)
        }

        val pc1 = perspectiveCameras.get(0)
        pc1.lookAt(0f, 0f, 1f)
        pc1.rotate(Vector3.Z, 180f)
        pc1.update()

        val pc2 = perspectiveCameras.get(1)
        pc2.lookAt(0f, 0f, -1f)
        pc2.rotate(Vector3.Z, 180f)
        pc2.update()

        // top
        val pc3 = perspectiveCameras.get(2)
        pc3.rotate(Vector3.Z, 90f)
        pc3.lookAt(0f, 1f, 0f)
        pc3.update()

        // down
        val pc4 = perspectiveCameras.get(3)
        pc4.rotate(Vector3.Z, 270f)
        pc4.lookAt(0f, -1f, 0f)
        pc4.update()

        // forward
        val pc5 = perspectiveCameras.get(4)
        pc5.lookAt(-1f, 0f, 0f)
        pc5.rotate(Vector3.X, 180f)
        pc5.update()

        // back
        val pc6 = perspectiveCameras.get(5)
        pc6.lookAt(1f, 0f, 0f)
        pc6.rotate(Vector3.X, 180f)
        pc6.update()

        var frameBufferCubemap: FrameBufferCubemap

        try {
            frameBufferCubemap = FrameBufferCubemap(Pixmap.Format.RGB888,
                    prefilterWidth.toInt(), prefilterHeight.toInt(), true)
        } catch (e: IllegalStateException) {
            frameBufferCubemap = FrameBufferCubemap(Pixmap.Format.RGB565,
                    prefilterWidth.toInt(), prefilterHeight.toInt(), true)
        }

        val cubemap = frameBufferCubemap.colorBufferTexture
        cubemap.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear)
        cubemap.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge)

        Gdx.gl.glBindTexture(cubemap.glTarget, cubemap.textureObjectHandle)
        Gdx.gl.glGenerateMipmap(GL20.GL_TEXTURE_CUBE_MAP)

        prefilterShader.begin()
        prefilterShader.setUniformMatrix("u_projection", perspectiveCameras.first().projection)
        prefilterShader.setUniformi("u_environment", 0)
        frameBufferCubemap.begin()
        environmentCubemap!!.bind(0)

        val maxMipLevels = 5

        for (mip in 0 until maxMipLevels) {
            // resize framebuffer according to mip-level size.
            val ml = Math.pow(0.5, mip.toDouble())

            val mipWidth = (prefilterWidth * ml).toInt()
            val mipHeight = (prefilterHeight * ml).toInt()

            Gdx.gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, frameBufferCubemap.depthBufferHandle)
            Gdx.gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER, GL20.GL_DEPTH_COMPONENT16, mipWidth, mipHeight)
            Gdx.gl.glViewport(0, 0, mipWidth, mipHeight)

            val roughness = mip.toFloat() / (maxMipLevels - 1).toFloat()
            prefilterShader.setUniformf("u_roughness", roughness)

            for (i in 0..5) {
                prefilterShader.setUniformMatrix("u_view", perspectiveCameras.get(i).view)

                val side = Cubemap.CubemapSide.values()[i]
                Gdx.gl20.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, side.glEnum,
                        cubemap.textureObjectHandle, mip)

                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
                cubeMesh!!.render(prefilterShader, GL20.GL_TRIANGLES)
            }
        }
        frameBufferCubemap.end()
        prefilterShader.end()
        prefilterShader.dispose()

        return frameBufferCubemap.colorBufferTexture
    }

    private fun renderBRDF(): Texture {
        val folder = "shaders/"

        val brdfShader = ShaderProgram(
                Gdx.files.internal(folder + "brdf_shader.vert"),
                Gdx.files.internal(folder + "brdf_shader.frag"))

        val shaderLog = brdfShader.log

        if (!brdfShader.isCompiled) {
            throw GdxRuntimeException(shaderLog)
        }

        var frameBuffer: FrameBuffer

        try {
            frameBuffer = FrameBuffer(Pixmap.Format.RGB888, brdfWidth.toInt(), brdfHeight.toInt(), true)
        } catch (e: IllegalStateException) {
            frameBuffer = FrameBuffer(Pixmap.Format.RGB565, brdfWidth.toInt(), brdfHeight.toInt(), true)
        }

        brdfShader.begin()
        frameBuffer.begin()
        Gdx.gl.glViewport(0, 0, brdfWidth.toInt(), brdfHeight.toInt())
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        quadMesh!!.render(brdfShader, GL20.GL_TRIANGLES)
        frameBuffer.end()
        brdfShader.end()
        brdfShader.dispose()

        return frameBuffer.colorBufferTexture
    }
}