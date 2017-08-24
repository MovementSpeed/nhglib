package io.github.movementspeed.nhglib.data.models.serialization;

import com.artemis.Component;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.graphics.scenes.SceneGraph;
import io.github.movementspeed.nhglib.interfaces.JsonParseable;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public abstract class ComponentJson implements JsonParseable<Component> {
    public int parentEntity;
    public int entity;

    public Nhg nhg;
    public SceneGraph sceneGraph;

    protected Component output;

    @Override
    public Component get() {
        return output;
    }
}
