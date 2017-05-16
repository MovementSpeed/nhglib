package io.github.voidzombie.nhglib.data.models.serialization.physics.shapes;

import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.utils.data.VectorPool;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public class BoxShapeJson extends ShapeJson {
    private btBoxShape shape;

    @Override
    public void parse(JsonValue jsonValue) {
        float width = jsonValue.getFloat("width", 0.0f) * 0.5f;
        float height = jsonValue.getFloat("height", 0.0f) * 0.5f;
        float depth = jsonValue.getFloat("depth", 0.0f) * 0.5f;

        shape = new btBoxShape(VectorPool.getVector3().set(width, height, depth));
    }

    @Override
    public btBoxShape get() {
        return shape;
    }
}
