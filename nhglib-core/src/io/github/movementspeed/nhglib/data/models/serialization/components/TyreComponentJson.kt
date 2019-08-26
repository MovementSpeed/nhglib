package io.github.movementspeed.nhglib.data.models.serialization.components

import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.core.ecs.components.physics.VehicleComponent
import io.github.movementspeed.nhglib.core.ecs.components.physics.TyreComponent
import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson
import io.github.movementspeed.nhglib.data.models.serialization.Vector3Json

/**
 * Created by Fausto Napoli on 11/06/2017.
 */
class TyreComponentJson : ComponentJson() {
    override fun parse(wheelJson: JsonValue) {
        var tyreComponent: TyreComponent? = null
        val vehicleComponent = nhg!!.entities.getComponent(parentEntity, VehicleComponent::class.java)

        if (vehicleComponent != null) {
            val attachmentPointJson = Vector3Json()
            attachmentPointJson.parse(wheelJson.get("attachmentPoint"))

            val directionJson = Vector3Json()
            directionJson.parse(wheelJson.get("direction"))

            val axisJson = Vector3Json()
            axisJson.parse(wheelJson.get("axis"))

            val wheelIndex = wheelJson.getInt("index")

            val radius = wheelJson.getFloat("radius", 0.1f)
            val suspensionRestLength = wheelJson.getFloat("suspensionRestLength", radius * 0.3f)
            val wheelFriction = wheelJson.getFloat("friction", 1f)

            val frontWheel = wheelJson.getBoolean("frontTyre", false)

            tyreComponent = nhg!!.entities.createComponent(entity, TyreComponent::class.java)
            tyreComponent!!.index = wheelIndex
            tyreComponent.suspensionRestLength = suspensionRestLength
            tyreComponent.wheelFriction = wheelFriction
            tyreComponent.radius = radius
            tyreComponent.frontTyre = frontWheel
            tyreComponent.attachmentPoint = attachmentPointJson.get()
            tyreComponent.direction = directionJson.get()
            tyreComponent.axis = axisJson.get()
            tyreComponent.vehicleComponent = vehicleComponent
        }

        output = tyreComponent
    }
}
