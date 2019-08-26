package io.github.movementspeed.nhglib.data.models.serialization.components

import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.core.ecs.components.physics.VehicleComponent
import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson
import io.github.movementspeed.nhglib.data.models.serialization.physics.shapes.ShapeJson
import io.github.movementspeed.nhglib.data.models.serialization.physics.vehicles.VehicleTuningJson

/**
 * Created by Fausto Napoli on 11/06/2017.
 */
class VehicleComponentJson : ComponentJson() {

    override fun parse(jsonValue: JsonValue) {
        val vehicleComponent = nhg!!.entities.createComponent(entity, VehicleComponent::class.java)

        // Shape
        val shapeJson = ShapeJson()

        if (jsonValue.has("shape")) {
            shapeJson.parse(jsonValue.get("shape"))
        }

        // Vehicle tuning
        val vehicleTuningJson = VehicleTuningJson()

        if (jsonValue.has("vehicleTuning")) {
            vehicleTuningJson.parse(jsonValue.get("vehicleTuning"))
        }

        val mass = jsonValue.getFloat("mass", 1f)
        val friction = jsonValue.getFloat("friction", 5f)
        val restitution = jsonValue.getFloat("restitution", 0f)

        val group = jsonValue.getShort("group", (-1).toShort())

        val maskList = jsonValue.get("mask")
        val masks: ShortArray

        if (maskList != null) {
            masks = maskList.asShortArray()
        } else {
            masks = shortArrayOf()
        }

        vehicleComponent.mass = mass
        vehicleComponent.friction = friction
        vehicleComponent.restitution = restitution
        vehicleComponent.collisionFiltering = true
        vehicleComponent.rigidBodyShape = shapeJson.get()

        if (group.toInt() != -1) {
            vehicleComponent.group = (1 shl group).toShort()
        } else {
            vehicleComponent.collisionFiltering = false
        }

        if (masks.size > 0) {
            if (masks[0].toInt() != -1) {
                vehicleComponent.mask = (1 shl masks[0]).toShort()
            } else {
                vehicleComponent.mask = 0
            }

            for (i in 1 until masks.size) {
                vehicleComponent.mask = vehicleComponent.mask or (1 shl masks[i]).toShort()
            }
        } else {
            vehicleComponent.collisionFiltering = false
        }

        vehicleComponent.vehicleTuning = vehicleTuningJson.get()

        output = vehicleComponent
    }
}