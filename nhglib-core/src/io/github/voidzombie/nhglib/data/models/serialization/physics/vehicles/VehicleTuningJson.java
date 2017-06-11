package io.github.voidzombie.nhglib.data.models.serialization.physics.vehicles;

import com.badlogic.gdx.physics.bullet.dynamics.btRaycastVehicle;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;

/**
 * Created by Fausto Napoli on 11/06/2017.
 */
public class VehicleTuningJson implements JsonParseable<btRaycastVehicle.btVehicleTuning> {
    private btRaycastVehicle.btVehicleTuning vehicleTuning;

    @Override
    public void parse(JsonValue jsonValue) {
        vehicleTuning = new btRaycastVehicle.btVehicleTuning();

        if (jsonValue.has("suspensionDumping")) {
            float value = jsonValue.getFloat("suspensionDumping");
            vehicleTuning.setSuspensionDamping(value);
        }

        if (jsonValue.has("suspensionCompression")) {
            float value = jsonValue.getFloat("suspensionCompression");
            vehicleTuning.setSuspensionCompression(value);
        }

        if (jsonValue.has("suspensionStiffness")) {
            float value = jsonValue.getFloat("suspensionStiffness");
            vehicleTuning.setSuspensionStiffness(value);
        }

        if (jsonValue.has("maxSuspensionTravelCm")) {
            float value = jsonValue.getFloat("maxSuspensionTravelCm");
            vehicleTuning.setMaxSuspensionTravelCm(value);
        }

        if (jsonValue.has("maxSuspensionForce")) {
            float value = jsonValue.getFloat("maxSuspensionForce");
            vehicleTuning.setMaxSuspensionForce(value);
        }

        if (jsonValue.has("frictionSlip")) {
            float value = jsonValue.getFloat("frictionSlip");
            vehicleTuning.setFrictionSlip(value);
        }
    }

    @Override
    public btRaycastVehicle.btVehicleTuning get() {
        return vehicleTuning;
    }
}
