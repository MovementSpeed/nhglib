package io.github.voidzombie.nhglib.data.models.serialization;

import com.artemis.Component;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.graphics.scenes.SceneGraph;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public abstract class ComponentJson implements JsonParseable<Component> {
    public Integer parentEntity;
    public Integer entity;

    public Nhg nhg;
    public SceneGraph sceneGraph;

    protected Component output;

    @Override
    public Component get() {
        return output;
    }
}
