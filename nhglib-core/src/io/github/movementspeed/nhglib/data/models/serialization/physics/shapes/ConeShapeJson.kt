package io.github.movementspeed.nhglib.data.models.serialization.physics.shapes;

import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.physics.models.ConeRigidBodyShape;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public class ConeShapeJson extends ShapeJson {
    private ConeRigidBodyShape shape;

    @Override
    public void parse(JsonValue jsonValue) {
        float radius = jsonValue.getFloat("radius", 1.0f);
        float height = jsonValue.getFloat("height", 1.0f);

        shape = new ConeRigidBodyShape(radius, height);
    }

    @Override
    public ConeRigidBodyShape get() {
        return shape;
    }
}
