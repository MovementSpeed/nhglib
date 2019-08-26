package io.github.movementspeed.nhglib.core.ecs.systems.base

import com.artemis.Aspect
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.core.ecs.interfaces.RenderingSystemInterface
import io.github.movementspeed.nhglib.core.ecs.systems.impl.CameraSystem
import io.github.movementspeed.nhglib.core.ecs.systems.impl.RenderingSystem
import io.github.movementspeed.nhglib.core.ecs.utils.Entities

abstract class BaseRenderingSystem(aspect: Aspect.Builder, private val entities: Entities) : NhgIteratingSystem(aspect), RenderingSystemInterface {
    private var added: Boolean = false

    protected var renderingSystem: RenderingSystem? = null
    protected var cameraSystem: CameraSystem? = null

    protected var cameras: Array<Camera>? = null
    override var renderableProviders: Array<RenderableProvider>
        protected set

    init {

        renderableProviders = Array()
    }

    override fun begin() {
        super.begin()

        if (!added) {
            added = true
            val rs = entities.getEntitySystem(RenderingSystem::class.java)

            rs?.addRenderingInterfaces(this)
        }

        if (cameras == null) {
            cameras = cameraSystem!!.cameras
        }
    }

    override fun onPreRender() {

    }

    override fun onPostRender() {

    }

    override fun clearRenderableProviders() {
        renderableProviders.clear()
    }

    override fun onUpdatedRenderer(renderingWidth: Int, renderingHeight: Int) {

    }
}
