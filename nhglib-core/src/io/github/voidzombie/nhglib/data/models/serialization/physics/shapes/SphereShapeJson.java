package io.github.voidzombie.nhglib.data.models.serialization.physics.shapes;

import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public class SphereShapeJson extends ShapeJson {
    private btSphereShape shape;

    @Override
    public void parse(JsonValue jsonValue) {
        float radius = jsonValue.getFloat("radius");
        shape = new btSphereShape(radius);
    }

    @Override
    public btSphereShape get() {
        return shape;
    }
}
