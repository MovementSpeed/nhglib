package io.github.movementspeed.nhglib.core.ecs.systems.impl

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import io.github.movementspeed.nhglib.core.ecs.components.graphics.LightComponent
import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent

/**
 * Created by Fausto Napoli on 14/03/2017.
 */
class LightingSystem : IteratingSystem(Aspect.all(NodeComponent::class.java, LightComponent::class.java)) {
    private val nodeMapper: ComponentMapper<NodeComponent>? = null
    private val lightMapper: ComponentMapper<LightComponent>? = null

    private val tempMat: Matrix4
    private val tempVec1: Vector3
    private val tempVec2: Vector3

    init {

        tempMat = Matrix4()
        tempVec1 = Vector3()
        tempVec2 = Vector3()
    }

    override fun process(entityId: Int) {
        val light = lightMapper!!.get(entityId)

        if (light.light!!.enabled) {
            val node = nodeMapper!!.get(entityId)

            light.light!!.position.set(node.translation)
            light.light!!.transform = node.transform

            /*switch (light.type) {
                case SPOT_LIGHT:
                case DIRECTIONAL_LIGHT:
                    tempMat.set(node.getTransform());
                    tempMat.translate(0f, 1f, 0f);

                    tempVec1.set(light.light.position)
                            .sub(tempMat.getTranslation(tempVec2));

                    light.light.direction.set(tempVec1);
                    break;
            }*/
        }
    }
}
