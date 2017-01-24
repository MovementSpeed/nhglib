package io.github.voidzombie.nhglib.graphics.scenes;

import com.artemis.Archetype;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class SceneGraph {
    private int rootEntity;

    private Archetype sceneEntityArchetype;
    private NodeComponent rootNodeComponent;

    private Array<Integer> entities;
    private ArrayMap<String, Integer> entityIds;

    @SuppressWarnings("unchecked")
    public SceneGraph(String rootId) {
        entities = new Array<>();
        entityIds = new ArrayMap<>();
        sceneEntityArchetype = Nhg.entitySystem.createArchetype(NodeComponent.class);

        rootEntity = createSceneEntity(rootId);
        rootNodeComponent = Nhg.entitySystem.getComponent(
                rootEntity, NodeComponent.class);
        rootNodeComponent.id = rootEntity;

        entities.add(rootEntity);
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

        NodeComponent nodeComponent = Nhg.entitySystem.getComponent(
                entity, NodeComponent.class);
        nodeComponent.id = entity;

        rootNodeComponent.node.addChild(nodeComponent.node);
        entities.add(entity);
        return entity;
    }

    public int addSceneEntity(String id, int parentEntity) {
        int entity = createSceneEntity(id);

        NodeComponent nodeComponent = Nhg.entitySystem
                .getComponent(entity, NodeComponent.class);
        nodeComponent.id = entity;

        NodeComponent parentNodeComponent = Nhg.entitySystem
                .getComponent(parentEntity, NodeComponent.class);

        parentNodeComponent.node.addChild(nodeComponent.node);
        entities.add(entity);
        return entity;
    }

    public int getSceneEntity(String id) {
        return entityIds.get(id);
    }

    public Array<Integer> getEntities() {
        return entities;
    }

    private int createSceneEntity(String id) {
        int entity = Nhg.entitySystem.createEntity(sceneEntityArchetype);
        entityIds.put(id, entity);

        return entity;
    }
}
