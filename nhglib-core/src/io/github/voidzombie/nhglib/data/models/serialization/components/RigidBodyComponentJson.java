package io.github.voidzombie.nhglib.data.models.serialization.components;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.data.models.serialization.ComponentJson;
import io.github.voidzombie.nhglib.data.models.serialization.physics.ActivationState;
import io.github.voidzombie.nhglib.data.models.serialization.physics.shapes.ShapeJson;
import io.github.voidzombie.nhglib.runtime.ecs.components.physics.RigidBodyComponent;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public class RigidBodyComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue jsonValue) {
        RigidBodyComponent rigidBodyComponent = entities.createComponent(entity, RigidBodyComponent.class);

        ShapeJson shapeJson = new ShapeJson();
        shapeJson.parse(jsonValue.get("shape"));

        String activationStateString = jsonValue.getString("activationState", "wantsDeactivation");
        ActivationState activationStateJson = ActivationState.fromString(activationStateString);

        int activationState = activationStateJson.state;

        float mass = jsonValue.getFloat("mass", 1.0f);
        float friction = jsonValue.getFloat("friction", 0.5f);
        float restitution = jsonValue.getFloat("restitution", 0f);

        btCollisionShape collisionShape = shapeJson.get();

        rigidBodyComponent.build(collisionShape, mass, friction, restitution);
        output = rigidBodyComponent;
    }
}
