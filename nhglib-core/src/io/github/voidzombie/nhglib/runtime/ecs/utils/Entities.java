package io.github.voidzombie.nhglib.runtime.ecs.utils;

import com.artemis.*;

/**
 * Created by Fausto Napoli on 07/12/2016.
 */
public class Entities {
    private World entityWorld;

    public void update(float delta) {
        entityWorld.setDelta(delta);
        entityWorld.process();
    }

    public void setEntityWorld(World entityWorld) {
        this.entityWorld = entityWorld;
    }

    public void removeComponent(int entity, Class<? extends Component> type) {
        entityWorld.edit(entity).remove(type);
    }

    public int createEntity() {
        return entityWorld.create();
    }

    public int createEntity(Archetype archetype) {
        return entityWorld.create(archetype);
    }

    public Archetype createArchetype(Class<? extends Component> ... components) {
        return new ArchetypeBuilder()
                .add(components)
                .build(entityWorld);
    }

    public <T extends BaseEntitySystem> T getEntitySystem(Class<T> systemClass) {
        return entityWorld.getSystem(systemClass);
    }

    public <T extends Component> T createComponent(int entity, Class<T> type) {
        return entityWorld.getMapper(type).create(entity);
    }

    public <T extends Component> T getComponent(int entity, Class<T> type) {
        return entityWorld.getMapper(type).get(entity);
    }

    public <T extends Component> ComponentMapper<T> getMapper(Class<T> type) {
        return entityWorld.getMapper(type);
    }
}
