package io.github.movementspeed.nhglib.data.models.serialization.physics.shapes;

import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.physics.models.SphereRigidBodyShape;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public class SphereShapeJson extends ShapeJson {
    private SphereRigidBodyShape shape;

    @Override
    public void parse(JsonValue jsonValue) {
        float radius = jsonValue.getFloat("radius");
        shape = new SphereRigidBodyShape(radius);
    }

    @Override
    public SphereRigidBodyShape get() {
        return shape;
    }
}
