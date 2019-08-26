package io.github.movementspeed.nhglib.data.models.serialization.components

import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.assets.Asset
import io.github.movementspeed.nhglib.core.ecs.components.graphics.UiComponent
import io.github.movementspeed.nhglib.data.models.serialization.AssetJson
import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson

class UiComponentJson : ComponentJson() {
    override fun parse(jsonValue: JsonValue) {
        val uiComponent = nhg!!.entities.createComponent(entity, UiComponent::class.java)
        uiComponent.fileName = jsonValue.getString("fileName", "")

        val uiType = jsonValue.getString("uiType", "screen").toLowerCase()

        if (uiType.contentEquals("screen")) {
            uiComponent.type = UiComponent.Type.SCREEN
        } else if (uiType.contentEquals("panel")) {
            uiComponent.type = UiComponent.Type.PANEL
        }

        val dependenciesJson = jsonValue.get("dependencies")

        for (i in 0 until dependenciesJson.size) {
            val assetJson = AssetJson()
            assetJson.parse(dependenciesJson.get(i))

            val dependency = assetJson.get()
            uiComponent.dependencies.add(dependency)
        }

        val actorNamesJson = jsonValue.get("actors")

        for (i in 0 until actorNamesJson.size) {
            val actor = actorNamesJson.getString(i)
            uiComponent.actorNames.add(actor)
        }

        output = uiComponent
    }
}