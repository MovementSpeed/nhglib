package io.github.voidzombie.nhglib.data.models;

import com.artemis.Component;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class ComponentJson implements JsonParseable<Component> {
    public Integer entity;
    private Component output;

    @Override
    public void parse(JsonValue jsonValue) {
        String type = jsonValue.getString("type");
        output = NHG.utils.sceneUtils.componentFromType(entity, type);

        if (output != null) {
            // TODO : we already have a component of a specific class, we "just" need to set its fields... Even recursively.
        }
    }

    @Override
    public Component get() {
        return output;
    }
}
