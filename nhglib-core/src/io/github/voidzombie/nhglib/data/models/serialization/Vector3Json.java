package io.github.voidzombie.nhglib.data.models.serialization;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;

/**
 * Created by Fausto Napoli on 11/06/2017.
 */
public class Vector3Json implements JsonParseable<Vector3> {
    private Vector3 vec;

    @Override
    public void parse(JsonValue jsonValue) {
        vec = new Vector3(
                jsonValue.getFloat("x"),
                jsonValue.getFloat("y"),
                jsonValue.getFloat("z"));
    }

    @Override
    public Vector3 get() {
        return vec;
    }
}
