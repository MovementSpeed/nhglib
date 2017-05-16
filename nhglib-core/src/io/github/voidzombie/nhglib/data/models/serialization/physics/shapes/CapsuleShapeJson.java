package io.github.voidzombie.nhglib.data.models.serialization.physics.shapes;

import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public class CapsuleShapeJson extends ShapeJson {
    private btCapsuleShape shape;

    @Override
    public void parse(JsonValue jsonValue) {
        float radius = jsonValue.getFloat("radius", 1.0f);
        float height = jsonValue.getFloat("height", 1.0f);

        shape = new btCapsuleShape(radius, height);
    }

    @Override
    public btCapsuleShape get() {
        return shape;
    }
}
