package io.github.movementspeed.nhglib.data.models.serialization.physics.shapes

import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.physics.models.CapsuleRigidBodyShape

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
class CapsuleShapeJson : ShapeJson() {
    private var shape: CapsuleRigidBodyShape? = null

    override fun parse(jsonValue: JsonValue) {
        val radius = jsonValue.getFloat("radius", 1.0f)
        val height = jsonValue.getFloat("height", 1.0f)

        shape = CapsuleRigidBodyShape(radius, height)
    }

    override fun get(): CapsuleRigidBodyShape? {
        return shape
    }
}
