package io.github.movementspeed.nhglib.data.models.serialization.components;

import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.core.ecs.components.physics.VehicleComponent;
import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson;
import io.github.movementspeed.nhglib.data.models.serialization.physics.shapes.ShapeJson;
import io.github.movementspeed.nhglib.data.models.serialization.physics.vehicles.VehicleTuningJson;

/**
 * Created by Fausto Napoli on 11/06/2017.
 */
public class VehicleComponentJson extends ComponentJson {

    @Override
    public void parse(JsonValue jsonValue) {
        VehicleComponent vehicleComponent = nhg.entities.createComponent(entity, VehicleComponent.class);

        // Shape
        ShapeJson shapeJson = new ShapeJson();

        if (jsonValue.has("shape")) {
            shapeJson.parse(jsonValue.get("shape"));
        }

        // Vehicle tuning
        VehicleTuningJson vehicleTuningJson = new VehicleTuningJson();

        if (jsonValue.has("vehicleTuning")) {
            vehicleTuningJson.parse(jsonValue.get("vehicleTuning"));
        }

        float mass = jsonValue.getFloat("mass", 1f);
        float friction = jsonValue.getFloat("friction", 5f);
        float restitution = jsonValue.getFloat("restitution", 0f);

        short group = jsonValue.getShort("group", (short) -1);

        JsonValue maskList = jsonValue.get("mask");
        short[] masks;

        if (maskList != null) {
            masks = maskList.asShortArray();
        } else {
            masks = new short[]{};
        }

        vehicleComponent.mass = mass;
        vehicleComponent.friction = friction;
        vehicleComponent.restitution = restitution;
        vehicleComponent.collisionFiltering = true;
        vehicleComponent.rigidBodyShape = shapeJson.get();

        if (group != -1) {
            vehicleComponent.group = (short) (1 << group);
        } else {
            vehicleComponent.collisionFiltering = false;
        }

        if (masks.length > 0) {
            if (masks[0] != -1) {
                vehicleComponent.mask = (short) (1 << masks[0]);
            } else {
                vehicleComponent.mask = 0;
            }

            for (int i = 1; i < masks.length; i++) {
                vehicleComponent.mask |= (short) (1 << masks[i]);
            }
        } else {
            vehicleComponent.collisionFiltering = false;
        }

        vehicleComponent.vehicleTuning = vehicleTuningJson.get();

        output = vehicleComponent;
    }
}