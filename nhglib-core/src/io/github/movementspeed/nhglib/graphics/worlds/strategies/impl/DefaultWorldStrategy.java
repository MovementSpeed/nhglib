package io.github.movementspeed.nhglib.graphics.worlds.strategies.impl;

import io.github.movementspeed.nhglib.graphics.scenes.Scene;
import io.github.movementspeed.nhglib.graphics.worlds.strategies.base.WorldStrategy;
import io.github.movementspeed.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.movementspeed.nhglib.utils.data.Bounds;

/**
 * Created by Fausto Napoli on 29/12/2016.
 * Handles small sized worlds.
 */
public class DefaultWorldStrategy extends WorldStrategy {
    @Override
    public void update(Scene scene, Bounds bounds, NodeComponent referenceNodeComponent) {
    }
}
