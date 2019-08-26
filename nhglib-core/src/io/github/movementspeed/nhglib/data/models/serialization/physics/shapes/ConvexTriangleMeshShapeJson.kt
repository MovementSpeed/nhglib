package io.github.movementspeed.nhglib.data.models.serialization.physics.shapes;

import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.physics.models.ConvexTriangleMeshRigidBodyShape;

/**
 * Created by Fausto Napoli on 15/06/2017.
 */
public class ConvexTriangleMeshShapeJson extends ShapeJson {
    private ConvexTriangleMeshRigidBodyShape shape;

    @Override
    public void parse(JsonValue jsonValue) {
        boolean calcAabb = jsonValue.getBoolean("calcAabb", true);
        String asset = jsonValue.getString("asset", "");

        shape = new ConvexTriangleMeshRigidBodyShape(asset, calcAabb);
    }

    @Override
    public ConvexTriangleMeshRigidBodyShape get() {
        return shape;
    }
}
