package io.github.movementspeed.nhglib.graphics.shaders.shadows.system.classical

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.graphics.rendering.RenderPass
import io.github.movementspeed.nhglib.graphics.shaders.attributes.ShadowSystemAttribute
import io.github.movementspeed.nhglib.graphics.shaders.shadows.system.ShadowSystem

class ClassicalShadowsRenderPass : RenderPass(false) {
    private var shadowSystem: ShadowSystem? = null
    private var passBatches: Array<ModelBatch>? = null

    override fun created() {
        shadowSystem = (environment?.get(ShadowSystemAttribute.Type) as? ShadowSystemAttribute)?.shadowSystem
        passBatches = Array()

        repeat(shadowSystem?.passQuantity ?: 0) {
            passBatches?.add(ModelBatch(shadowSystem?.getPassShaderProvider(it)))
        }
    }

    override fun begin(camera: PerspectiveCamera) {}

    override fun render(camera: PerspectiveCamera, renderableProviders: Array<RenderableProvider>) {
        shadowSystem?.begin(camera, renderableProviders)
        shadowSystem?.update()

        repeat(shadowSystem?.passQuantity ?: 0) { i ->
            shadowSystem?.begin(i)
            var lightCamera = shadowSystem?.next()

            while (lightCamera != null) {
                passBatches?.get(i)?.begin(lightCamera)
                passBatches?.get(i)?.render(renderableProviders, environment)
                passBatches?.get(i)?.end()

                lightCamera = shadowSystem?.next()
            }

            shadowSystem?.end(i)
        }

        shadowSystem?.end()
    }

    override fun end() {
        mainFBO?.begin()
    }
}
