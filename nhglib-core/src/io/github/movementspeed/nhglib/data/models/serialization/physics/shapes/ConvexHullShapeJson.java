package io.github.movementspeed.nhglib.data.models.serialization.physics.shapes;

import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.physics.models.ConvexHullRigidBodyShape;

/**
 * Created by Fausto Napoli on 15/06/2017.
 */
public class ConvexHullShapeJson extends ShapeJson {
    private ConvexHullRigidBodyShape shape;

    @Override
    public void parse(JsonValue jsonValue) {
        boolean optimize = jsonValue.getBoolean("optimize", false);
        String asset = jsonValue.getString("asset", "");

        shape = new ConvexHullRigidBodyShape(asset, optimize);
    }

    @Override
    public ConvexHullRigidBodyShape get() {
        return shape;
    }
}
