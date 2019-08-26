package io.github.movementspeed.nhglib.data.models.serialization.components

import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.core.ecs.components.physics.RigidBodyComponent
import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson
import io.github.movementspeed.nhglib.data.models.serialization.physics.shapes.ShapeJson

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
class RigidBodyComponentJson : ComponentJson() {
    override fun parse(jsonValue: JsonValue) {
        val rigidBodyComponent = nhg!!.entities.createComponent(entity, RigidBodyComponent::class.java)

        val shapeJson = ShapeJson()
        shapeJson.parse(jsonValue.get("shape"))

        val kinematic = jsonValue.getBoolean("kinematic", false)

        val mass = jsonValue.getFloat("mass", 1.0f)
        val friction = jsonValue.getFloat("friction", 0.5f)
        val restitution = jsonValue.getFloat("restitution", 0f)

        val group = jsonValue.getShort("group", (-1).toShort())

        val maskList = jsonValue.get("mask")
        val masks: ShortArray

        if (maskList != null) {
            masks = maskList.asShortArray()
        } else {
            masks = shortArrayOf()
        }

        rigidBodyComponent.mass = mass
        rigidBodyComponent.friction = friction
        rigidBodyComponent.restitution = restitution
        rigidBodyComponent.kinematic = kinematic
        rigidBodyComponent.collisionFiltering = true
        rigidBodyComponent.rigidBodyShape = shapeJson.get()

        if (group.toInt() != -1) {
            rigidBodyComponent.group = (1 shl group).toShort()
        } else {
            rigidBodyComponent.collisionFiltering = false
        }

        if (masks.size > 0) {
            if (masks[0].toInt() != -1) {
                rigidBodyComponent.mask = (1 shl masks[0]).toShort()
            } else {
                rigidBodyComponent.mask = 0
            }

            for (i in 1 until masks.size) {
                rigidBodyComponent.mask = rigidBodyComponent.mask or (1 shl masks[i]).toShort()
            }
        } else {
            rigidBodyComponent.collisionFiltering = false
        }

        output = rigidBodyComponent
    }
}
