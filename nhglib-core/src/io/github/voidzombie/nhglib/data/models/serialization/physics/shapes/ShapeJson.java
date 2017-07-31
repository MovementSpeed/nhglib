package io.github.voidzombie.nhglib.data.models.serialization.physics.shapes;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;
import io.github.voidzombie.nhglib.physics.enums.RigidBodyType;
import io.github.voidzombie.nhglib.physics.models.RigidBodyShape;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public class ShapeJson implements JsonParseable<RigidBodyShape> {
    public RigidBodyShape output;

    @Override
    public void parse(JsonValue shapeJson) {
        RigidBodyType type = RigidBodyType.fromString(shapeJson.getString("type"));

        switch (type) {
            case SPHERE:
                SphereShapeJson sphereShapeJson = new SphereShapeJson();
                sphereShapeJson.parse(shapeJson);
                output = sphereShapeJson.get();
                break;

            case BOX:
                BoxShapeJson boxShapeJson = new BoxShapeJson();
                boxShapeJson.parse(shapeJson);
                output = boxShapeJson.get();
                break;

            case CYLINDER:
                CylinderShapeJson cylinderShapeJson = new CylinderShapeJson();
                cylinderShapeJson.parse(shapeJson);
                output = cylinderShapeJson.get();
                break;

            case CONE:
                ConeShapeJson coneShapeJson = new ConeShapeJson();
                coneShapeJson.parse(shapeJson);
                output = coneShapeJson.get();
                break;

            case CAPSULE:
                CapsuleShapeJson capsuleShapeJson = new CapsuleShapeJson();
                capsuleShapeJson.parse(shapeJson);
                output = capsuleShapeJson.get();
                break;

            case CONVEX_HULL:
                ConvexHullShapeJson convexHullShapeJson = new ConvexHullShapeJson();
                convexHullShapeJson.parse(shapeJson);
                output = convexHullShapeJson.get();
                break;

            case BVH_TRIANGLE_MESH:
                BvhTriangleMeshShapeJson bvhTriangleMeshShapeJson = new BvhTriangleMeshShapeJson();
                bvhTriangleMeshShapeJson.parse(shapeJson);
                output = bvhTriangleMeshShapeJson.get();
                break;

            case CONVEX_TRIANGLE_MESH:
                ConvexTriangleMeshShapeJson convexTriangleMeshShapeJson = new ConvexTriangleMeshShapeJson();
                convexTriangleMeshShapeJson.parse(shapeJson);
                output = convexTriangleMeshShapeJson.get();
                break;
        }

        output.type = type;
    }

    @Override
    public RigidBodyShape get() {
        return output;
    }
}
