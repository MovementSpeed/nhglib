package io.github.movementspeed.nhglib.data.models.serialization.components

import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.core.ecs.components.graphics.ParticleEffectComponent
import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
class ParticleEffectComponentJson : ComponentJson() {
    override fun parse(jsonValue: JsonValue) {
        val particleEffectComponent = nhg!!.entities.createComponent(entity, ParticleEffectComponent::class.java)

        particleEffectComponent.asset = jsonValue.getString("asset", "")
        output = particleEffectComponent
    }
}
