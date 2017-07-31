package io.github.voidzombie.nhglib.data.models.serialization.physics.shapes;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.physics.models.CapsuleRigidBodyShape;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public class CapsuleShapeJson extends ShapeJson {
    private CapsuleRigidBodyShape shape;

    @Override
    public void parse(JsonValue jsonValue) {
        float radius = jsonValue.getFloat("radius", 1.0f);
        float height = jsonValue.getFloat("height", 1.0f);

        shape = new CapsuleRigidBodyShape(radius, height);
    }

    @Override
    public CapsuleRigidBodyShape get() {
        return shape;
    }
}
