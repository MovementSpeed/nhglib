package io.github.movementspeed.nhglib.core.ecs.systems.impl

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelCache
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.graphics.g3d.model.Node
import io.github.movementspeed.nhglib.assets.Assets
import io.github.movementspeed.nhglib.core.ecs.components.graphics.ModelComponent
import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent
import io.github.movementspeed.nhglib.core.ecs.systems.base.BaseRenderingSystem
import io.github.movementspeed.nhglib.core.ecs.utils.Entities
import io.github.movementspeed.nhglib.core.messaging.Messaging
import io.github.movementspeed.nhglib.utils.debug.NhgLogger

class ModelRenderingSystem(entities: Entities, private val messaging: Messaging, private val assets: Assets) : BaseRenderingSystem(Aspect.all(NodeComponent::class.java, ModelComponent::class.java), entities) {
    private var buildingModelCache = false
    private val staticCache: ModelCache

    private val nodeMapper: ComponentMapper<NodeComponent>? = null
    private val modelMapper: ComponentMapper<ModelComponent>? = null

    init {
        staticCache = ModelCache()
    }

    override fun begin() {
        super.begin()

        if (!buildingModelCache) renderableProviders.add(staticCache)
    }

    override fun process(entityId: Int) {
        val modelComponent = modelMapper!!.get(entityId)
        val nodeComponent = nodeMapper!!.get(entityId)

        if (modelComponent.enabled &&
                cameras!!.size > 0 &&
                modelComponent.state == ModelComponent.State.READY) {

            val camera = cameras!!.first()

            if (!modelComponent.nodeAdded) {
                modelComponent.nodeAdded = true

                for (i in 0 until modelComponent.model.nodes.size) {
                    val n = modelComponent.model.nodes.get(i)
                    nodeComponent.node.addChild(n)
                }

                val parentInternalNodeId = nodeComponent.parentInternalNodeId

                if (parentInternalNodeId != null && !parentInternalNodeId.isEmpty()) {
                    val parentNodeComponent = nodeComponent.parentNodeComponent
                    val parentInternalNode = parentNodeComponent!!.node.getChild(parentInternalNodeId, true, false)

                    if (parentInternalNode != null) {
                        parentNodeComponent.node.removeChild(nodeComponent.node)
                        parentInternalNode.addChild(nodeComponent.node)
                    }
                }
            }

            modelComponent.calculateTransforms()

            if (modelComponent.type == ModelComponent.Type.DYNAMIC) {
                if (camera.frustum.sphereInFrustum(nodeComponent.translation, modelComponent.radius)) {
                    if (modelComponent.animationController != null) {
                        modelComponent.animationController.update(Gdx.graphics.deltaTime)
                    }

                    renderableProviders.add(modelComponent.model)
                }
            } else if (modelComponent.type == ModelComponent.Type.STATIC) {
                if (!modelComponent.cached) {
                    modelComponent.cached = true
                    rebuildCache(modelComponent.model)
                }
            }
        }
    }

    override fun dispose() {
        super.dispose()
        staticCache.dispose()
    }

    private fun rebuildCache(vararg renderableProviders: RenderableProvider) {
        if (cameras!!.size > 0) {
            NhgLogger.log(this, "Rebuilding model cache.")
            buildingModelCache = true
            val previousCache = ModelCache()
            val camera = cameras!!.first()

            previousCache.begin(camera)
            previousCache.add(staticCache)
            previousCache.end()

            staticCache.begin(camera)
            staticCache.add(previousCache)

            for (provider in renderableProviders) {
                staticCache.add(provider)
            }

            staticCache.end()
            previousCache.dispose()
            buildingModelCache = false
        }
    }
}
