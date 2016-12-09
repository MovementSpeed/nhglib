package io.github.voidzombie.nhglib.scenes;

import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class SceneGraph {
    private int rootEntity;
    private NodeComponent rootNodeComponent;

    public SceneGraph() {
        rootEntity = NHG.entitySystem.createEntity();
        rootNodeComponent = NHG.entitySystem.createComponent(
                rootEntity, NodeComponent.class);
    }

    public int getRootEntity() {
        return rootEntity;
    }

    /**
     * Adds an entity to the root node.
     * @return the created entity.
     */
    public int addEntity() {
        int entity = NHG.entitySystem.createEntity();

        NodeComponent nodeComponent = NHG.entitySystem
                .createComponent(entity, NodeComponent.class);

        rootNodeComponent.node.addChild(nodeComponent.node);
        return entity;
    }

    public int addEntity(int parentEntity) {
        int entity = NHG.entitySystem.createEntity();

        NodeComponent nodeComponent = NHG.entitySystem
                .createComponent(entity, NodeComponent.class);

        NodeComponent parentNodeComponent = NHG.entitySystem
                .getComponent(parentEntity, NodeComponent.class);

        parentNodeComponent.node.addChild(nodeComponent.node);
        return entity;
    }
}
