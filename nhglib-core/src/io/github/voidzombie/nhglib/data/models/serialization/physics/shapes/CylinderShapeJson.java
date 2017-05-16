package io.github.voidzombie.nhglib.data.models.serialization.physics.shapes;

import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.utils.data.VectorPool;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public class CylinderShapeJson extends ShapeJson {
    private btCylinderShape shape;

    @Override
    public void parse(JsonValue jsonValue) {
        float width = jsonValue.getFloat("width", 0.0f) * 0.5f;
        float height = jsonValue.getFloat("height", 0.0f) * 0.5f;
        float depth = jsonValue.getFloat("depth", 0.0f) * 0.5f;

        shape = new btCylinderShape(VectorPool.getVector3().set(width, height, depth));
    }

    @Override
    public btCylinderShape get() {
        return shape;
    }
}
