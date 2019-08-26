package io.github.movementspeed.nhglib.data.models.serialization.components;

import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.core.ecs.components.physics.VehicleComponent;
import io.github.movementspeed.nhglib.core.ecs.components.physics.TyreComponent;
import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson;
import io.github.movementspeed.nhglib.data.models.serialization.Vector3Json;

/**
 * Created by Fausto Napoli on 11/06/2017.
 */
public class TyreComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue wheelJson) {
        TyreComponent tyreComponent = null;
        VehicleComponent vehicleComponent = nhg.entities.getComponent(parentEntity, VehicleComponent.class);

        if (vehicleComponent != null) {
            Vector3Json attachmentPointJson = new Vector3Json();
            attachmentPointJson.parse(wheelJson.get("attachmentPoint"));

            Vector3Json directionJson = new Vector3Json();
            directionJson.parse(wheelJson.get("direction"));

            Vector3Json axisJson = new Vector3Json();
            axisJson.parse(wheelJson.get("axis"));

            int wheelIndex = wheelJson.getInt("index");

            float radius = wheelJson.getFloat("radius", 0.1f);
            float suspensionRestLength = wheelJson.getFloat("suspensionRestLength", radius * 0.3f);
            float wheelFriction = wheelJson.getFloat("friction", 1f);

            boolean frontWheel = wheelJson.getBoolean("frontTyre", false);

            tyreComponent = nhg.entities.createComponent(entity, TyreComponent.class);
            tyreComponent.index = wheelIndex;
            tyreComponent.suspensionRestLength = suspensionRestLength;
            tyreComponent.wheelFriction = wheelFriction;
            tyreComponent.radius = radius;
            tyreComponent.frontTyre = frontWheel;
            tyreComponent.attachmentPoint = attachmentPointJson.get();
            tyreComponent.direction = directionJson.get();
            tyreComponent.axis = axisJson.get();
            tyreComponent.vehicleComponent = vehicleComponent;
        }

        output = tyreComponent;
    }
}
