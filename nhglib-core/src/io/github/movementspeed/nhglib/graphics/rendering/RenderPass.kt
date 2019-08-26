package io.github.movementspeed.nhglib.graphics.rendering

import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.utils.data.Bundle

abstract class RenderPass(outputData: Boolean) {
    protected var outputData: Bundle? = null
    protected var previousRenderPass: RenderPass? = null
    protected var mainFBO: FrameBuffer? = null
    protected var renderer: ModelBatch? = null
    protected var environment: Environment? = null

    protected var shaderProvider: ShaderProvider? = null
        set(value) {
            field = value
            this.renderer = ModelBatch(value)
        }

    init {
        if (outputData) {
            this.outputData = Bundle()
        }
    }

    abstract fun created()
    abstract fun begin(camera: PerspectiveCamera)
    abstract fun render(camera: PerspectiveCamera, renderableProviders: Array<RenderableProvider>)
    abstract fun end()
}
