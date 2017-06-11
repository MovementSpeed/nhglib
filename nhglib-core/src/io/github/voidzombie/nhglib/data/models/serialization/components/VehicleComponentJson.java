package io.github.voidzombie.nhglib.data.models.serialization.components;

import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.data.models.serialization.ComponentJson;
import io.github.voidzombie.nhglib.data.models.serialization.physics.shapes.ShapeJson;
import io.github.voidzombie.nhglib.data.models.serialization.physics.vehicles.VehicleTuningJson;
import io.github.voidzombie.nhglib.runtime.ecs.components.physics.VehicleComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.PhysicsSystem;

/**
 * Created by Fausto Napoli on 11/06/2017.
 */
public class VehicleComponentJson extends ComponentJson {

    @Override
    public void parse(JsonValue jsonValue) {
        VehicleComponent vehicleComponent = entities.createComponent(entity, VehicleComponent.class);

        PhysicsSystem physicsSystem = entities.getEntitySystem(PhysicsSystem.class);
        btDynamicsWorld world = physicsSystem.getBulletWorld();

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

        vehicleComponent.build(world, shapeJson.get(), vehicleTuningJson.get(), mass, friction, restitution);
        output = vehicleComponent;
    }
}