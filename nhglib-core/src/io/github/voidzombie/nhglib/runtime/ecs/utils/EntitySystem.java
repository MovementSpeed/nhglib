package io.github.voidzombie.nhglib.runtime.ecs.utils;

import com.artemis.Archetype;
import com.artemis.Component;
import com.artemis.World;

/**
 * Created by Fausto Napoli on 07/12/2016.
 */
public class EntitySystem {
    private World entityWorld;

    public void update(float delta) {
        entityWorld.setDelta(delta);
        entityWorld.process();
    }

    public void setEntityWorld(World entityWorld) {
        this.entityWorld = entityWorld;
    }

    public int createEntity() {
        return entityWorld.create();
    }

    public int createEntity(Archetype archetype) {
        return entityWorld.create(archetype);
    }

    public <T extends Component> T createComponent(int entity, Class<T> type) {
        return entityWorld.getMapper(type).create(entity);
    }

    public <T extends Component> T getComponent(int entity, Class<T> type) {
        return entityWorld.getMapper(type).get(entity);
    }
}
