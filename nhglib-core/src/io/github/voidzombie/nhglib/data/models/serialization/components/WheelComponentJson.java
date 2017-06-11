package io.github.voidzombie.nhglib.data.models.serialization.components;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.data.models.serialization.ComponentJson;
import io.github.voidzombie.nhglib.data.models.serialization.Vector3Json;
import io.github.voidzombie.nhglib.runtime.ecs.components.physics.VehicleComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.physics.WheelComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;

/**
 * Created by Fausto Napoli on 11/06/2017.
 */
public class WheelComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue wheelJson) {
        Vector3Json attachmentPointJson = new Vector3Json();
        attachmentPointJson.parse(wheelJson.get("attachmentPoint"));

        Vector3Json directionJson = new Vector3Json();
        directionJson.parse(wheelJson.get("direction"));

        Vector3Json axisJson = new Vector3Json();
        axisJson.parse(wheelJson.get("axis"));

        float radius = wheelJson.getFloat("radius", 0.1f);
        float suspensionRestLength = wheelJson.getFloat("suspensionRestLength", radius * 0.3f);
        float wheelFriction = wheelJson.getFloat("friction", 5f);

        boolean frontWheel = wheelJson.getBoolean("frontWheel", false);

        VehicleComponent vehicleComponent = entities.getComponent(parentEntity, VehicleComponent.class);

        vehicleComponent.addWheel(attachmentPointJson.get(), directionJson.get(), axisJson.get(), radius,
                suspensionRestLength, wheelFriction, frontWheel);

        WheelComponent wheelComponent = entities.createComponent(entity, WheelComponent.class);
        wheelComponent.build(vehicleComponent.getVehicle(), vehicleComponent.getWheelNumber() - 1);

        NodeComponent nodeComponent = entities.getComponent(entity, NodeComponent.class);
        nodeComponent.node.inheritTransform = false;
    }
}
