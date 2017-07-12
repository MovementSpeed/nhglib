package io.github.voidzombie.nhglib.data.models.serialization.components;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.data.models.serialization.ComponentJson;
import io.github.voidzombie.nhglib.data.models.serialization.physics.shapes.ShapeJson;
import io.github.voidzombie.nhglib.runtime.ecs.components.physics.RigidBodyComponent;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public class RigidBodyComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue jsonValue) {
        RigidBodyComponent rigidBodyComponent = nhg.entities.createComponent(entity, RigidBodyComponent.class);

        ShapeJson shapeJson = new ShapeJson();
        shapeJson.nhg = nhg;
        shapeJson.parse(jsonValue.get("shape"));

        float mass = jsonValue.getFloat("mass", 1.0f);
        float friction = jsonValue.getFloat("friction", 0.5f);
        float restitution = jsonValue.getFloat("restitution", 0f);

        short group = jsonValue.getShort("group", (short) -1);

        JsonValue maskList = jsonValue.get("mask");
        short[] masks;

        if (maskList != null) {
            masks = maskList.asShortArray();
        } else {
            masks = new short[]{};
        }

        btCollisionShape collisionShape = shapeJson.get();

        rigidBodyComponent.build(collisionShape, mass, friction, restitution, group, masks);
        output = rigidBodyComponent;
    }
}
