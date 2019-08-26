package io.github.movementspeed.nhglib.data.models.serialization.components

import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.core.ecs.components.graphics.ModelComponent
import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson
import io.github.movementspeed.nhglib.data.models.serialization.PBRMaterialJson

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
class ModelComponentJson : ComponentJson() {
    override fun parse(jsonValue: JsonValue) {
        val modelComponent = nhg!!.entities.createComponent(entity, ModelComponent::class.java)

        val type = jsonValue.getString("graphicsType")
        val asset = jsonValue.getString("asset", "")

        val enabled = jsonValue.getBoolean("enabled", true)

        val materialsJson = jsonValue.get("materials")

        if (materialsJson != null) {
            for (mat in materialsJson) {
                val pbrMaterialJson = PBRMaterialJson()
                pbrMaterialJson.parse(mat)

                modelComponent.pbrMaterials.add(pbrMaterialJson.get())
            }
        }

        modelComponent.type = ModelComponent.Type.fromString(type)
        modelComponent.asset = asset
        modelComponent.enabled = enabled
        output = modelComponent
    }
}
