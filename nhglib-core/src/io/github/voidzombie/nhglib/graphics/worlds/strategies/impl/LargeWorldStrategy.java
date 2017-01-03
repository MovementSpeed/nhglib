package io.github.voidzombie.nhglib.graphics.worlds.strategies.impl;

import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.graphics.worlds.strategies.base.WorldStrategy;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.utils.data.Bounds;

/**
 * Created by Fausto Napoli on 29/12/2016.
 * Handles large sized worlds.
 */
public class LargeWorldStrategy extends WorldStrategy {
    @Override
    public void update(Scene scene, Bounds bounds, NodeComponent referenceNodeComponent) {
        if (referenceNodeComponent != null) {
            Bounds.Info boundsInfo = bounds.boundsInfo(referenceNodeComponent.getTranslation());

            if (!boundsInfo.inBounds) {
                NodeComponent rootNodeComponent = NHG.entitySystem.getComponent(
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