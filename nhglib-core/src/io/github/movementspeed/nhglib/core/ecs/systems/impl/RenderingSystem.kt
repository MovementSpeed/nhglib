package io.github.movementspeed.nhglib.core.ecs.systems.impl

import com.artemis.BaseSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import io.github.movementspeed.nhglib.Nhg
import io.github.movementspeed.nhglib.core.ecs.interfaces.RenderingSystemInterface
import io.github.movementspeed.nhglib.core.ecs.systems.base.BaseRenderingSystem
import io.github.movementspeed.nhglib.graphics.rendering.RenderPass
import io.github.movementspeed.nhglib.graphics.shaders.tiled.TiledPBRRenderPass
import io.github.movementspeed.nhglib.utils.graphics.GLUtils

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
class RenderingSystem : BaseSystem(), Disposable {

    private var saveScreenMode: Boolean = false

    // Injected references
    private val cameraSystem: CameraSystem? = null

    val environment: Environment
    private var clearColor: Color? = null
    private val fpsLogger: FPSLogger
    private var frameBuffer: FrameBuffer? = null
    private val spriteBatch: SpriteBatch

    var screenPixmap: Pixmap? = null
        private set

    private val renderPasses: Array<RenderPass>
    private val renderableProviders: Array<RenderableProvider>
    private val renderingInterfaces: Array<RenderingSystemInterface>

    init {
        clearColor = Color.BLACK
        fpsLogger = FPSLogger()
        environment = Environment()

        renderPasses = Array()
        renderableProviders = Array()
        renderingInterfaces = Array()

        spriteBatch = SpriteBatch()
        spriteBatch.enableBlending()

        setRenderScale(1f)
        setRenderPass(0, TiledPBRRenderPass())
    }

    override fun processSystem() {
        if (Nhg.debugLogs && Nhg.debugFpsLogs) {
            fpsLogger.log()
        }

        if (cameraSystem!!.cameras.size > 0) {
            val camera = cameraSystem.cameras.first() as PerspectiveCamera

            for (rsi in renderingInterfaces) {
                rsi.onPreRender()
            }

            renderableProviders.clear()

            for (rsi in renderingInterfaces) {
                val providers = rsi.renderableProviders
                renderableProviders.addAll(providers)
                rsi.clearRenderableProviders()
            }

            if (renderableProviders.size > 0) {
                frameBuffer!!.begin()
                GLUtils.clearScreen(clearColor)

                for (i in 0 until renderPasses.size) {
                    val renderPass = renderPasses.get(i)

                    if (i > 0) {
                        renderPass.setPreviousRenderPass(renderPasses.get(i - 1))
                    }

                    renderPass.begin(camera)
                    renderPass.render(camera, renderableProviders)
                    renderPass.end()
                }

                if (saveScreenMode) {
                    screenPixmap = getScreenPixmapInternal(frameBuffer!!.width, frameBuffer!!.height)
                    saveScreenMode = false
                }

                frameBuffer!!.end()

                spriteBatch.begin()
                spriteBatch.draw(frameBuffer!!.colorBufferTexture,
                        0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(),
                        0f, 0f, 1f, 1f)
                spriteBatch.end()

                for (rsi in renderingInterfaces) {
                    rsi.onPostRender()
                }
            }
        }
    }

    override fun dispose() {
        super.dispose()
        frameBuffer!!.dispose()
    }

    fun beginScreenPixmap() {
        saveScreenMode = true
    }

    fun endScreenPixmap() {
        screenPixmap!!.dispose()
        screenPixmap = null
    }

    fun setRenderPass(index: Int, renderPass: RenderPass) {
        if (index >= 0) {
            if (index >= renderPasses.size) {
                renderPasses.setSize(index + 1)
            }

            renderPass.setEnvironment(environment)
            renderPass.setMainFBO(frameBuffer)

            if (index > 0) {
                renderPass.setPreviousRenderPass(renderPasses.get(index - 1))
            }

            renderPass.created()
            renderPasses.set(index, renderPass)
        }
    }

    fun setRenderScale(renderScale: Float) {
        var renderScale = renderScale
        if (renderScale < 0f) renderScale = 0f
        if (renderScale > 1f) renderScale = 1f

        renderWidth = Math.round(Gdx.graphics.width * renderScale)
        renderHeight = Math.round(Gdx.graphics.height * renderScale)

        updateFramebuffer(renderWidth, renderHeight)
    }

    fun setRenderResolution(renderWidth: Int, renderHeight: Int) {
        var renderWidth = renderWidth
        var renderHeight = renderHeight
        if (renderWidth < 1) renderWidth = 1
        if (renderHeight < 1) renderHeight = 1

        RenderingSystem.renderWidth = renderWidth
        RenderingSystem.renderHeight = renderHeight

        updateFramebuffer(renderWidth, renderHeight)
    }

    fun setClearColor(clearColor: Color?) {
        if (clearColor != null) {
            this.clearColor = clearColor
        }
    }

    fun addRenderingInterfaces(vararg renderingSystems: BaseRenderingSystem) {
        for (brs in renderingSystems) {
            renderingInterfaces.add(brs)
        }
    }

    fun addRenderingInterfaces(vararg renderingSystemInterfaces: RenderingSystemInterface) {
        for (rsi in renderingSystemInterfaces) {
            renderingInterfaces.add(rsi)
        }
    }

    fun getDepthTexture(): Texture? {
        return null
    }

    private fun updateFramebuffer(width: Int, height: Int) {
        if (frameBuffer != null) {
            frameBuffer!!.dispose()
        }

        try {
            frameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, width, height, true)
        } catch (e: IllegalStateException) {
            frameBuffer = FrameBuffer(Pixmap.Format.RGB565, width, height, true)
        }

        for (rsi in renderingInterfaces) {
            rsi.onUpdatedRenderer(width, height)
        }
    }

    private fun getScreenPixmapInternal(width: Int, height: Int): Pixmap {
        val pixels = ScreenUtils.getFrameBufferPixels(0, 0, width, height, true)

        // this loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
        var i = 4
        while (i < pixels.size) {
            pixels[i - 1] = 255.toByte()
            i += 4
        }

        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
        BufferUtils.copy(pixels, 0, pixmap.pixels, pixels.size)
        return pixmap
    }

    companion object {
        var renderWidth: Int = 0
        var renderHeight: Int = 0
        var depthTexture: Texture? = null
    }
}
