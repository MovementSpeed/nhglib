package io.github.voidzombie.nhglib.data.models;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class TransformJson implements JsonParseable<TransformJson> {
    public Vector3 position;
    public Vector3 rotation;
    public Vector3 scale;

    public TransformJson() {
        position = new Vector3();
        rotation = new Vector3();
        scale = new Vector3();
    }

    @Override
    public void parse(JsonValue jsonValue) {
        JsonValue positionJson = jsonValue.get("position");
        JsonValue rotationJson = jsonValue.get("rotation");
        JsonValue scaleJson = jsonValue.get("scale");

        float xPosition = positionJson.getFloat("x");
        float yPosition = positionJson.getFloat("y");
        float zPosition = positionJson.getFloat("z");

        float xRotation = rotationJson.getFloat("x");
        float yRotation = rotationJson.getFloat("y");
        float zRotation = rotationJson.getFloat("z");

        float xScale = scaleJson.getFloat("x");
        float yScale = scaleJson.getFloat("y");
        float zScale = scaleJson.getFloat("z");

        position.set(xPosition, yPosition, zPosition);
        rotation.set(xRotation, yRotation, zRotation);
        scale.set(xScale, yScale, zScale);
    }

    @Override
    public TransformJson get() {
        return this;
    }
}
