package io.github.voidzombie.nhglib.scenes;

import com.artemis.Archetype;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class SceneGraph {
    private int rootEntity;
    private NodeComponent rootNodeComponent;
    private Archetype sceneEntityArchetype;

    @SuppressWarnings("unchecked")
    public SceneGraph() {
        sceneEntityArchetype = NHG.entitySystem.createArchetype(NodeComponent.class);

        rootEntity = NHG.entitySystem.createEntity(sceneEntityArchetype);
        rootNodeComponent = NHG.entitySystem.getComponent(
                rootEntity, NodeComponent.class);
        rootNodeComponent.id = rootEntity;
    }

    public int getRootEntity() {
        return rootEntity;
    }

    /**
     * Adds an entity to the root node.
     * @return the created entity.
     */
    public int addSceneEntity() {
        int entity = createSceneEntity();
        NodeComponent nodeComponent = NHG.entitySystem.getComponent(
                entity, NodeComponent.class);
        nodeComponent.id = entity;

        rootNodeComponent.node.addChild(nodeComponent.node);
        return entity;
    }

    public int addSceneEntity(int parentEntity) {
        int entity = createSceneEntity();
        NodeComponent nodeComponent = NHG.entitySystem
                .getComponent(entity, NodeComponent.class);
        nodeComponent.id = entity;

        NodeComponent parentNodeComponent = NHG.entitySystem
                .getComponent(parentEntity, NodeComponent.class);

        parentNodeComponent.node.addChild(nodeComponent.node);
        return entity;
    }

    private int createSceneEntity() {
        return NHG.entitySystem.createEntity(sceneEntityArchetype);
    }
}
