package io.github.movementspeed.nhglib.data.models.serialization;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.interfaces.JsonParseable;

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

        float xPosition = 0;
        float yPosition = 0;
        float zPosition = 0;

        float xRotation = 0;
        float yRotation = 0;
        float zRotation = 0;

        float xScale = 1;
        float yScale = 1;
        float zScale = 1;

        if (positionJson != null) {
            xPosition = positionJson.getFloat("x");
            yPosition = positionJson.getFloat("y");
            zPosition = positionJson.getFloat("z");
        }

        if (rotationJson != null) {
            xRotation = rotationJson.getFloat("x");
            yRotation = rotationJson.getFloat("y");
            zRotation = rotationJson.getFloat("z");
        }

        if (scaleJson != null) {
            xScale = scaleJson.getFloat("x");
            yScale = scaleJson.getFloat("y");
            zScale = scaleJson.getFloat("z");
        }

        position.set(xPosition, yPosition, zPosition);
        rotation.set(xRotation, yRotation, zRotation);
        scale.set(xScale, yScale, zScale);
    }

    @Override
    public TransformJson get() {
        return this;
    }
}
