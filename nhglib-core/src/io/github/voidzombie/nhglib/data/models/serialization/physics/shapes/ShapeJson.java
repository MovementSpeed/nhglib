package io.github.voidzombie.nhglib.data.models.serialization.physics.shapes;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.data.models.serialization.physics.RigidBodyType;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public class ShapeJson implements JsonParseable<btCollisionShape> {
    public Nhg nhg;
    private btCollisionShape shape;

    @Override
    public void parse(JsonValue shapeJson) {
        RigidBodyType type = RigidBodyType.fromString(shapeJson.getString("type"));

        switch (type) {
            case SPHERE:
                SphereShapeJson sphereShapeJson = new SphereShapeJson();
                sphereShapeJson.nhg = nhg;
                sphereShapeJson.parse(shapeJson);
                shape = sphereShapeJson.get();
                break;

            case BOX:
                BoxShapeJson boxShapeJson = new BoxShapeJson();
                boxShapeJson.nhg = nhg;
                boxShapeJson.parse(shapeJson);
                shape = boxShapeJson.get();
                break;

            case CYLINDER:
                CylinderShapeJson cylinderShapeJson = new CylinderShapeJson();
                cylinderShapeJson.nhg = nhg;
                cylinderShapeJson.parse(shapeJson);
                shape = cylinderShapeJson.get();
                break;

            case CONE:
                ConeShapeJson coneShapeJson = new ConeShapeJson();
                coneShapeJson.nhg = nhg;
                coneShapeJson.parse(shapeJson);
                shape = coneShapeJson.get();
                break;

            case CAPSULE:
                CapsuleShapeJson capsuleShapeJson = new CapsuleShapeJson();
                capsuleShapeJson.nhg = nhg;
                capsuleShapeJson.parse(shapeJson);
                shape = capsuleShapeJson.get();
                break;

            case CONVEX_HULL:
                ConvexHullShapeJson convexHullShapeJson = new ConvexHullShapeJson();
                convexHullShapeJson.nhg = nhg;
                convexHullShapeJson.parse(shapeJson);
                shape = convexHullShapeJson.get();
                break;
        }
    }

    @Override
    public btCollisionShape get() {
        return shape;
    }
}
