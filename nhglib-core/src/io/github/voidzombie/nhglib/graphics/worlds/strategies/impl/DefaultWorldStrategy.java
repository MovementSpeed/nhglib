package io.github.voidzombie.nhglib.graphics.worlds.strategies.impl;

import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.graphics.worlds.strategies.base.WorldStrategy;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.utils.data.Bounds;

/**
 * Created by Fausto Napoli on 29/12/2016.
 * Handles small/medium sized worlds.
 */
public class DefaultWorldStrategy extends WorldStrategy {
    @Override
    public void update(Scene scene, Bounds bounds, NodeComponent referenceNodeComponent) {
    }
}
