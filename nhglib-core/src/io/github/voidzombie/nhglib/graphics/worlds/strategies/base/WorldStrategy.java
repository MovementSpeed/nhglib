package io.github.voidzombie.nhglib.graphics.worlds.strategies.base;

import io.github.voidzombie.nhglib.graphics.scenes.Scene;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.utils.data.Bounds;

/**
 * Created by Fausto Napoli on 29/12/2016.
 * Determines how to handle entities based on world size.
 */
public abstract class WorldStrategy {
    public abstract void update(Scene scene, Bounds bounds, NodeComponent referenceNodeComponent);
}
