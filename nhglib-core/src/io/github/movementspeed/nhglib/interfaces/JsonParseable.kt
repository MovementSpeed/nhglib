package io.github.movementspeed.nhglib.interfaces;

import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public interface JsonParseable<T> {
    void parse(JsonValue jsonValue);

    T get();
}
