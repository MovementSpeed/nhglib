package io.github.voidzombie.nhglib.data.models;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;
import io.github.voidzombie.nhglib.utils.graphics.TransformUtils;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class TransformJson implements JsonParseable {
    public Vector3 position;
    public Vector3 rotation;
    public Vector3 scale;

    public TransformJson() {
        position = TransformUtils.ZERO_VECTOR_3;
        rotation = TransformUtils.ZERO_VECTOR_3;
        scale = TransformUtils.ONE_VECTOR_3;
    }

    @Override
    public void parse(JsonValue jsonValue) {
        JsonValue positionJson = jsonValue.get("position");
        JsonValue rotationJson = jsonValue.get("rotation");
        JsonValue scaleJson = jsonValue.get("scale");

        position.set(
                positionJson.getFloat("x"),
                positionJson.getFloat("y"),
                positionJson.getFloat("z"));

        rotation.set(
                rotationJson.getFloat("x"),
                rotationJson.getFloat("y"),
                rotationJson.getFloat("z"));

        scale.set(
                scaleJson.getFloat("x"),
                scaleJson.getFloat("y"),
                scaleJson.getFloat("z"));
    }
}
