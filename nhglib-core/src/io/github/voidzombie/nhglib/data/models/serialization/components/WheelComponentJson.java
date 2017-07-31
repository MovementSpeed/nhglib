package io.github.voidzombie.nhglib.data.models.serialization.components;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.data.models.serialization.ComponentJson;
import io.github.voidzombie.nhglib.data.models.serialization.Vector3Json;
import io.github.voidzombie.nhglib.runtime.ecs.components.physics.VehicleComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.physics.WheelComponent;

/**
 * Created by Fausto Napoli on 11/06/2017.
 */
public class WheelComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue wheelJson) {
        WheelComponent wheelComponent = null;
        VehicleComponent vehicleComponent = nhg.entities.getComponent(parentEntity, VehicleComponent.class);

        if (vehicleComponent != null) {
            Vector3Json attachmentPointJson = new Vector3Json();
            attachmentPointJson.parse(wheelJson.get("attachmentPoint"));

            Vector3Json directionJson = new Vector3Json();
            directionJson.parse(wheelJson.get("direction"));

            Vector3Json axisJson = new Vector3Json();
            axisJson.parse(wheelJson.get("axis"));

            int wheelIndex = wheelJson.getInt("wheelIndex");

            float radius = wheelJson.getFloat("radius", 0.1f);
            float suspensionRestLength = wheelJson.getFloat("suspensionRestLength", radius * 0.3f);
            float wheelFriction = wheelJson.getFloat("friction", 5f);

            boolean frontWheel = wheelJson.getBoolean("frontWheel", false);

            wheelComponent = nhg.entities.createComponent(entity, WheelComponent.class);
            wheelComponent.wheelIndex = wheelIndex;
            wheelComponent.suspensionRestLength = suspensionRestLength;
            wheelComponent.wheelFriction = wheelFriction;
            wheelComponent.frontWheel = frontWheel;
            wheelComponent.attachmentPoint = attachmentPointJson.get();
            wheelComponent.direction = directionJson.get();
            wheelComponent.axis = axisJson.get();
        }

        output = wheelComponent;
    }
}
