package io.github.movementspeed.nhglib.graphics.worlds

import com.badlogic.gdx.utils.ArrayMap
import io.github.movementspeed.nhglib.assets.Assets
import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent
import io.github.movementspeed.nhglib.core.ecs.utils.Entities
import io.github.movementspeed.nhglib.core.messaging.Messaging
import io.github.movementspeed.nhglib.graphics.scenes.Scene
import io.github.movementspeed.nhglib.graphics.scenes.SceneManager
import io.github.movementspeed.nhglib.graphics.worlds.strategies.base.WorldStrategy
import io.github.movementspeed.nhglib.utils.data.Bounds

/**
 * Created by Fausto Napoli on 28/12/2016.
 * Manages how the engine should handle the game world\space.
 */
class NhgWorld(messaging: Messaging,
               private val entities: Entities,
               assets: Assets,
               private val worldStrategy: WorldStrategy,
               private val bounds: Bounds = Bounds(1f, 1f, 1f)) {
    val sceneManager = SceneManager(messaging, entities, assets)
    private var referenceNodeComponent: NodeComponent? = null

    private val scenes: ArrayMap<String, Scene> = ArrayMap()

    val currentScene: Scene?
        get() = sceneManager.currentScene

    fun addScene(scene: Scene) {
        scenes.put(scene.name, scene)
    }

    fun loadScene(scene: Scene) {
        addScene(scene)
        loadScene(scene.name)
    }

    fun loadScene(name: String?) {
        val scene = getScene(name)
        sceneManager.loadScene(scene)
    }

    fun unloadScene(name: String) {
        val scene = getScene(name)
        sceneManager.unloadScene(scene)
    }

    fun update() {
        worldStrategy.update(sceneManager.currentScene!!, bounds, referenceNodeComponent!!)
    }

    fun setReferenceEntity(entityName: String?) {
        if (entityName != null && entityName.isNotEmpty()) {
            val referenceEntity = sceneManager.currentScene!!.sceneGraph.getSceneEntity(entityName)
            referenceNodeComponent = entities.getComponent(referenceEntity, NodeComponent::class.java)
        }
    }

    fun getScene(name: String?): Scene {
        return scenes.get(name)
    }
}
