package io.github.movementspeed.nhglib.data.models.serialization.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.core.ecs.components.graphics.LightComponent
import io.github.movementspeed.nhglib.core.ecs.systems.impl.RenderingSystem
import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson
import io.github.movementspeed.nhglib.enums.LightType
import io.github.movementspeed.nhglib.graphics.lights.NhgLight
import io.github.movementspeed.nhglib.graphics.lights.NhgLightsAttribute

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
class LightComponentJson : ComponentJson() {
    override fun parse(jsonValue: JsonValue) {
        val renderingSystem = nhg!!.entities.getEntitySystem(RenderingSystem::class.java)
        val lightComponent = nhg!!.entities.createComponent(entity, LightComponent::class.java)

        val lightType = LightType.fromString(jsonValue.getString("lightType"))

        //boolean shadowLight = jsonValue.getBoolean("shadowLight", false);

        val range = jsonValue.getFloat("range", 1f)
        val intensity = jsonValue.getFloat("intensity", 1f)
        var innerAngle = jsonValue.getFloat("innerAngle", 0f)
        val outerAngle = jsonValue.getFloat("outerAngle", 0f)

        if (innerAngle > outerAngle) {
            innerAngle = outerAngle
        }

        val colorJson = jsonValue.get("color")
        var color = Color()

        if (colorJson != null) {
            color = Color(
                    colorJson.getFloat("r", 1f),
                    colorJson.getFloat("g", 1f),
                    colorJson.getFloat("b", 1f),
                    colorJson.getFloat("a", 1f))
        }

        val directionJson = jsonValue.get("direction")
        var direction = Vector3()

        if (directionJson != null) {
            direction = Vector3(
                    directionJson.getFloat("x", 0f),
                    directionJson.getFloat("y", 0f),
                    directionJson.getFloat("z", 0f))
        }

        var light: NhgLight? = null

        when (lightType) {
            LightType.DIRECTIONAL_LIGHT -> {
                light = NhgLight.directional(intensity, color)
                light!!.direction.set(direction)
            }

            LightType.POINT_LIGHT -> light = NhgLight.point(intensity, range, color)

            LightType.SPOT_LIGHT -> {
                light = NhgLight.spot(intensity, range, innerAngle, outerAngle, color)
                light!!.direction.set(direction)
            }
        }

        if (light == null) return

        light.enabled = jsonValue.getBoolean("enabled", true)

        val environment = renderingSystem.environment
        var attribute: NhgLightsAttribute? = environment
                .get(NhgLightsAttribute.Type) as NhgLightsAttribute

        if (attribute == null) {
            attribute = NhgLightsAttribute()
            environment.set(attribute)
        }

        attribute.lights.add(light)

        lightComponent.light = light
        lightComponent.type = lightType
        output = lightComponent
    }
}
