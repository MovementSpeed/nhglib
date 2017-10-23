package io.github.movementspeed.nhglib.graphics.worlds.strategies.impl;

import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent;
import io.github.movementspeed.nhglib.core.ecs.utils.Entities;
import io.github.movementspeed.nhglib.graphics.scenes.Scene;
import io.github.movementspeed.nhglib.graphics.worlds.strategies.base.WorldStrategy;
import io.github.movementspeed.nhglib.utils.data.Bounds;

/**
 * Created by Fausto Napoli on 29/12/2016.
 * Handles large sized worlds.
 */
public class LargeWorldStrategy extends WorldStrategy {
    private Entities entities;

    public LargeWorldStrategy(Entities entities) {
        this.entities = entities;
    }

    @Override
    public void update(Scene scene, Bounds bounds, NodeComponent referenceNodeComponent) {
        if (referenceNodeComponent != null) {
            Bounds.Info boundsInfo = bounds.boundsInfo(referenceNodeComponent.getTranslation());

            if (!boundsInfo.inBounds) {
                NodeComponent rootNodeComponent = entities.getComponent(
                        scene.sceneGraph.getRootEntity(),
                        NodeComponent.class);

                float xTranslation = bounds.getWidth() * boundsInfo.widthSide;
                float yTranslation = bounds.getHeight() * boundsInfo.heightSide;
                float zTranslation = bounds.getDepth() * boundsInfo.depthSide;

                rootNodeComponent.translate(-xTranslation, -yTranslation, -zTranslation, true);
            }
        }
    }
}