package io.github.movementspeed.nhglib.graphics.worlds.strategies.impl

import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent
import io.github.movementspeed.nhglib.graphics.scenes.Scene
import io.github.movementspeed.nhglib.graphics.worlds.strategies.base.WorldStrategy
import io.github.movementspeed.nhglib.utils.data.Bounds

/**
 * Created by Fausto Napoli on 29/12/2016.
 * Handles small sized worlds.
 */
class DefaultWorldStrategy : WorldStrategy() {
    override fun update(scene: Scene, bounds: Bounds, referenceNodeComponent: NodeComponent) {}
}
