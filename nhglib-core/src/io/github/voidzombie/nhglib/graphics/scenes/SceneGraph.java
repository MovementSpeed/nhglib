package io.github.voidzombie.nhglib.graphics.scenes;

import com.artemis.Archetype;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.utils.Entities;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class SceneGraph {
    private int rootEntity;

    private Entities entities;
    private Archetype sceneEntityArchetype;
    private NodeComponent rootNodeComponent;

    private Array<Integer> entitiesArray;
    private ArrayMap<String, Integer> entityIds;

    @SuppressWarnings("unchecked")
    public SceneGraph(Entities entities, String rootId) {
        this.entities = entities;

        this.entitiesArray = new Array<>();
        entityIds = new ArrayMap<>();
        sceneEntityArchetype = entities.createArchetype(NodeComponent.class);

        rootEntity = createSceneEntity(rootId);
        rootNodeComponent = entities.getComponent(
                rootEntity, NodeComponent.class);
        rootNodeComponent.id = rootEntity;

        this.entitiesArray.add(rootEntity);
    }

    public int getRootEntity() {
        return rootEntity;
    }

    /**
     * Adds an entity to the root node.
     * @return the created entity.
     */
    public int addSceneEntity(String id) {
        int entity = createSceneEntity(id);

        NodeComponent nodeComponent = entities.getComponent(
                entity, NodeComponent.class);
        nodeComponent.id = entity;

        rootNodeComponent.node.addChild(nodeComponent.node);
        entitiesArray.add(entity);
        return entity;
    }

    public int addSceneEntity(String id, int parentEntity) {
        int entity = createSceneEntity(id);

        NodeComponent nodeComponent = entities
                .getComponent(entity, NodeComponent.class);
        nodeComponent.id = entity;

        NodeComponent parentNodeComponent = entities
                .getComponent(parentEntity, NodeComponent.class);

        parentNodeComponent.node.addChild(nodeComponent.node);
        entitiesArray.add(entity);
        return entity;
    }

    public int getSceneEntity(String id) {
        return entityIds.get(id);
    }

    public Array<Integer> getEntities() {
        return entitiesArray;
    }

    private int createSceneEntity(String id) {
        int entity = entities.createEntity(sceneEntityArchetype);
        entityIds.put(id, entity);

        return entity;
    }
}
