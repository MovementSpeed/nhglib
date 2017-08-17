package io.github.movementspeed.nhglib.data.models.serialization.physics.shapes;

import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.physics.models.BoxRigidBodyShape;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public class BoxShapeJson extends ShapeJson {
    private BoxRigidBodyShape shape;

    @Override
    public void parse(JsonValue jsonValue) {
        float width = jsonValue.getFloat("width", 0.0f) * 0.5f;
        float height = jsonValue.getFloat("height", 0.0f) * 0.5f;
        float depth = jsonValue.getFloat("depth", 0.0f) * 0.5f;

        shape = new BoxRigidBodyShape(width, height, depth);
    }

    @Override
    public BoxRigidBodyShape get() {
        return shape;
    }
}
