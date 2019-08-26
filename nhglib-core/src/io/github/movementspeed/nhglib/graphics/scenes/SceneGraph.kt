package io.github.movementspeed.nhglib.graphics.scenes

import com.artemis.Archetype
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import io.github.movementspeed.nhglib.Nhg
import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
class SceneGraph(private val nhg: Nhg, rootId: String) {
    val rootEntity: Int
    private val sceneEntityArchetype: Archetype
    private val rootNodeComponent: NodeComponent

    val entities: Array<Int>
    private val entityIds: ArrayMap<String, Int>

    init {

        this.entities = Array()
        entityIds = ArrayMap()
        sceneEntityArchetype = nhg.entities.createArchetype(NodeComponent::class.java)

        rootEntity = createSceneEntity(rootId)
        rootNodeComponent = nhg.entities.getComponent(rootEntity, NodeComponent::class.java)
        rootNodeComponent.setId(rootEntity)

        this.entities.add(rootEntity)
    }

    fun createSceneEntity(id: String): Int {
        val entity = nhg.entities.createEntity(sceneEntityArchetype)
        entityIds.put(id, entity)

        return entity
    }

    @JvmOverloads
    fun addSceneEntity(entity: Int, parentEntity: Int = rootEntity): Int {
        val nodeComponent = nhg.entities
                .getComponent(entity, NodeComponent::class.java)
        nodeComponent.setId(entity)

        val parentNodeComponent = nhg.entities
                .getComponent(parentEntity, NodeComponent::class.java)

        parentNodeComponent.node.addChild<Node>(nodeComponent.node)
        nodeComponent.parentNodeComponent = parentNodeComponent

        entities.add(entity)
        return entity
    }

    @JvmOverloads
    fun addSceneEntity(id: String, parentEntity: Int = rootEntity): Int {
        val entity = createSceneEntity(id)
        return addSceneEntity(entity, parentEntity)
    }

    fun getSceneEntity(id: String): Int {
        return entityIds.get(id)
    }
}
