package io.github.movementspeed.nhglib.data.models.serialization.components

import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.core.ecs.components.common.MessageComponent
import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
class MessageComponentJson : ComponentJson() {
    override fun parse(jsonValue: JsonValue) {
        val messageComponent = nhg!!.entities.createComponent(entity, MessageComponent::class.java)

        val filters = jsonValue.get("filters")
        messageComponent.subscribe(*filters.asStringArray())

        output = messageComponent
    }
}
