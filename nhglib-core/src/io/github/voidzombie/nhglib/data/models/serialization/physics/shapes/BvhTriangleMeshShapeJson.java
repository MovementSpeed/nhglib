package io.github.voidzombie.nhglib.data.models.serialization.physics.shapes;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.physics.models.BvhTriangleMeshRigidBodyShape;

/**
 * Created by Fausto Napoli on 15/06/2017.
 */
public class BvhTriangleMeshShapeJson extends ShapeJson {
    private BvhTriangleMeshRigidBodyShape shape;

    @Override
    public void parse(JsonValue jsonValue) {
        boolean quantization = jsonValue.getBoolean("useQuantizedAabbCompression", true);
        boolean buildBvh = jsonValue.getBoolean("buildBvh", true);

        String asset = jsonValue.getString("asset", "");

        shape = new BvhTriangleMeshRigidBodyShape(asset, quantization, buildBvh);
    }

    @Override
    public BvhTriangleMeshRigidBodyShape get() {
        return shape;
    }
}
