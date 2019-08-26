package io.github.movementspeed.nhglib.graphics.shaders.tiled

import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.graphics.rendering.RenderPass

class TiledPBRRenderPass : RenderPass(false) {
    override fun created() {
        environment?.let {
            shaderProvider = TiledPBRShaderProvider(it)
        }
    }

    override fun begin(camera: PerspectiveCamera) {
        renderer?.begin(camera)
    }

    override fun render(camera: PerspectiveCamera, renderableProviders: Array<RenderableProvider>) {
        renderer?.render(renderableProviders, environment)
    }

    override fun end() {
        renderer?.end()
    }
}
