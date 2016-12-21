package io.github.voidzombie.nhglib.data.models;

import com.artemis.Component;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;
import io.github.voidzombie.nhglib.utils.scenes.SceneUtils;

import java.lang.reflect.Field;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public abstract class ComponentJson implements JsonParseable<Component> {
    public Integer entity;
    protected Component output;

    @Override
    public Component get() {
        return output;
    }
}
