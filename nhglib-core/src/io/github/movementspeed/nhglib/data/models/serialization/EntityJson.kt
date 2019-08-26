package io.github.movementspeed.nhglib.data.models.serialization

import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.Nhg
import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent
import io.github.movementspeed.nhglib.graphics.scenes.SceneGraph
import io.github.movementspeed.nhglib.interfaces.JsonParseable
import io.github.movementspeed.nhglib.utils.scenes.SceneMappings

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
class EntityJson(private val nhg: Nhg) : JsonParseable<Int> {
    var parentEntity: Int = 0

    private var output: Int = 0
    private var sceneGraph: SceneGraph? = null

    override fun parse(jsonValue: JsonValue) {
        val id = jsonValue.getString("id")
        val attachToParent = jsonValue.getBoolean("attachToParent", true)

        val entity: Int

        if (attachToParent) {
            entity = sceneGraph!!.addSceneEntity(id, parentEntity)
        } else {
            entity = sceneGraph!!.addSceneEntity(id)
        }

        val componentsJson = jsonValue.get("components")

        if (componentsJson != null) {
            for (componentJsonValue in componentsJson) {
                val type = componentJsonValue.getString("type")
                val componentJson = SceneMappings.componentJsonFromType(type)

                if (componentJson != null) {
                    componentJson.parentEntity = parentEntity
                    componentJson.entity = entity
                    componentJson.nhg = nhg
                    componentJson.sceneGraph = sceneGraph
                    componentJson.parse(componentJsonValue)
                }
            }
        }

        val entitiesJson = jsonValue.get("entities")

        if (entitiesJson != null) {
            for (entityJsonValue in entitiesJson) {
                val entityJson = EntityJson(nhg)
                entityJson.sceneGraph = sceneGraph
                entityJson.parentEntity = entity
                entityJson.parse(entityJsonValue)
            }
        }

        val parentInternalNodeId = jsonValue.getString("parentInternalNodeId", null)

        val transformJson = TransformJson()

        if (jsonValue.has("transform")) {
            transformJson.parse(jsonValue.get("transform"))

            val nodeComponent = nhg.entities.getComponent(entity, NodeComponent::class.java)
            nodeComponent.parentInternalNodeId = parentInternalNodeId
            nodeComponent.setTransform(
                    transformJson.position,
                    transformJson.rotation,
                    transformJson.scale)
        }

        output = entity
    }

    fun setParentEntity(parentEntity: Int) {
        this.parentEntity = parentEntity
    }

    fun setSceneGraph(sceneGraph: SceneGraph) {
        this.sceneGraph = sceneGraph
    }

    override fun get(): Int? {
        return output
    }
}
