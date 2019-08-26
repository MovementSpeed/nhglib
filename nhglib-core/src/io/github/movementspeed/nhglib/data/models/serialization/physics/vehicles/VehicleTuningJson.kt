package io.github.movementspeed.nhglib.data.models.serialization.physics.vehicles

import com.badlogic.gdx.physics.bullet.dynamics.btRaycastVehicle
import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.interfaces.JsonParseable

/**
 * Created by Fausto Napoli on 11/06/2017.
 */
class VehicleTuningJson : JsonParseable<btRaycastVehicle.btVehicleTuning> {
    private var vehicleTuning: btRaycastVehicle.btVehicleTuning? = null

    override fun parse(jsonValue: JsonValue) {
        vehicleTuning = btRaycastVehicle.btVehicleTuning()

        if (jsonValue.has("suspensionDamping")) {
            val value = jsonValue.getFloat("suspensionDamping")
            vehicleTuning!!.suspensionDamping = value
        }

        if (jsonValue.has("suspensionCompression")) {
            val value = jsonValue.getFloat("suspensionCompression")
            vehicleTuning!!.suspensionCompression = value
        }

        if (jsonValue.has("suspensionStiffness")) {
            val value = jsonValue.getFloat("suspensionStiffness")
            vehicleTuning!!.suspensionStiffness = value
        }

        if (jsonValue.has("maxSuspensionTravelCm")) {
            val value = jsonValue.getFloat("maxSuspensionTravelCm")
            vehicleTuning!!.maxSuspensionTravelCm = value
        }

        if (jsonValue.has("maxSuspensionForce")) {
            val value = jsonValue.getFloat("maxSuspensionForce")
            vehicleTuning!!.maxSuspensionForce = value
        }

        if (jsonValue.has("frictionSlip")) {
            val value = jsonValue.getFloat("frictionSlip")
            vehicleTuning!!.frictionSlip = value
        }
    }

    override fun get(): btRaycastVehicle.btVehicleTuning? {
        return vehicleTuning
    }
}
