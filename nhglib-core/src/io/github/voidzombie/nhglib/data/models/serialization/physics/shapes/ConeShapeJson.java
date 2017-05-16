package io.github.voidzombie.nhglib.data.models.serialization.physics.shapes;

import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public class ConeShapeJson extends ShapeJson {
    private btConeShape shape;

    @Override
    public void parse(JsonValue jsonValue) {
        float radius = jsonValue.getFloat("radius", 1.0f);
        float height = jsonValue.getFloat("height", 1.0f);

        shape = new btConeShape(radius, height);
    }

    @Override
    public btConeShape get() {
        return shape;
    }
}
