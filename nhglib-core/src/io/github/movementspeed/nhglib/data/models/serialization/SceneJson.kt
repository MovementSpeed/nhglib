package io.github.movementspeed.nhglib.data.models.serialization

import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.Nhg
import io.github.movementspeed.nhglib.assets.Asset
import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent
import io.github.movementspeed.nhglib.graphics.scenes.Scene
import io.github.movementspeed.nhglib.interfaces.JsonParseable

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
class SceneJson(private val nhg: Nhg) : JsonParseable<Scene> {
    private var output: Scene? = null

    override fun parse(jsonValue: JsonValue) {
        val name = jsonValue.getString("name")
        val entitiesJson = jsonValue.get("entities")
        val assetsJson = jsonValue.get("assets")

        output = Scene(nhg, "root")
        output!!.name = name

        for (assetJson in assetsJson) {
            val aj = AssetJson()
            aj.parse(assetJson)

            val asset = aj.get()
            output!!.assets.add(asset)
        }

        val rootEntity = output!!.sceneGraph.rootEntity

        if (entitiesJson != null) {
            for (entity in entitiesJson) {
                val entityJson = EntityJson(nhg)
                entityJson.setSceneGraph(output!!.sceneGraph)
                entityJson.setParentEntity(rootEntity)
                entityJson.parse(entity)
            }
        }

        val nodeComponent = nhg.entities.getComponent(rootEntity, NodeComponent::class.java)
        nodeComponent.applyTransforms()
    }

    override fun get(): Scene? {
        return output
    }
}
