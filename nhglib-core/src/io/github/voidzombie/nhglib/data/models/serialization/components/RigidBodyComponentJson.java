package io.github.voidzombie.nhglib.data.models.serialization.components;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.data.models.serialization.ComponentJson;
import io.github.voidzombie.nhglib.data.models.serialization.physics.ActivationState;
import io.github.voidzombie.nhglib.data.models.serialization.physics.RigidBodyType;
import io.github.voidzombie.nhglib.data.models.serialization.physics.shapes.*;
import io.github.voidzombie.nhglib.runtime.ecs.components.physics.RigidBodyComponent;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public class RigidBodyComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue jsonValue) {
        RigidBodyComponent rigidBodyComponent = entities.createComponent(entity, RigidBodyComponent.class);

        JsonValue shapeJson = jsonValue.get("shape");

        String activationStateString = jsonValue.getString("activationState", "wantsDeactivation");
        ActivationState activationStateJson = ActivationState.fromString(activationStateString);

        btCollisionShape collisionShape = getCollisionShape(shapeJson);
        int activationState = activationStateJson.state;
        float mass = jsonValue.getFloat("mass", 1.0f);
        float friction = jsonValue.getFloat("friction", 0.5f);
        float restitution = jsonValue.getFloat("restitution", 0f);

        rigidBodyComponent.build(collisionShape, activationState, mass, friction, restitution);
        output = rigidBodyComponent;
    }

    private btCollisionShape getCollisionShape(JsonValue shapeJson) {
        btCollisionShape shape = null;
        RigidBodyType type = RigidBodyType.fromString(shapeJson.getString("type"));

        switch (type) {
            case SPHERE:
                SphereShapeJson sphereShapeJson = new SphereShapeJson();
                sphereShapeJson.parse(shapeJson);
                shape = sphereShapeJson.get();
                break;

            case BOX:
                BoxShapeJson boxShapeJson = new BoxShapeJson();
                boxShapeJson.parse(shapeJson);
                shape = boxShapeJson.get();
                break;

            case CYLINDER:
                CylinderShapeJson cylinderShapeJson = new CylinderShapeJson();
                cylinderShapeJson.parse(shapeJson);
                shape = cylinderShapeJson.get();
                break;

            case CONE:
                ConeShapeJson coneShapeJson = new ConeShapeJson();
                coneShapeJson.parse(shapeJson);
                shape = coneShapeJson.get();
                break;

            case CAPSULE:
                CapsuleShapeJson capsuleShapeJson = new CapsuleShapeJson();
                capsuleShapeJson.parse(shapeJson);
                shape = capsuleShapeJson.get();
                break;
        }

        return shape;
    }
}
