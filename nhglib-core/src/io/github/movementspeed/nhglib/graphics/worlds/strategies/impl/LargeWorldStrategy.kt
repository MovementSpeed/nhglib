package io.github.movementspeed.nhglib.graphics.worlds.strategies.impl

import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent
import io.github.movementspeed.nhglib.core.ecs.utils.Entities
import io.github.movementspeed.nhglib.graphics.scenes.Scene
import io.github.movementspeed.nhglib.graphics.worlds.strategies.base.WorldStrategy
import io.github.movementspeed.nhglib.utils.data.Bounds

/**
 * Created by Fausto Napoli on 29/12/2016.
 * Handles large sized worlds.
 */
class LargeWorldStrategy(private val entities: Entities) : WorldStrategy() {
    override fun update(scene: Scene, bounds: Bounds, referenceNodeComponent: NodeComponent) {
        val boundsInfo = bounds.boundsInfo(referenceNodeComponent.getTranslation())

        if (!boundsInfo.inBounds) {
            val rootNodeComponent = entities.getComponent(
                    scene.sceneGraph.rootEntity,
                    NodeComponent::class.java)

            val xTranslation = bounds.width * boundsInfo.widthSide
            val yTranslation = bounds.height * boundsInfo.heightSide
            val zTranslation = bounds.depth * boundsInfo.depthSide

            rootNodeComponent.translate(-xTranslation, -yTranslation, -zTranslation, true)
        }
    }
}