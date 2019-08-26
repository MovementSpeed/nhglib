package io.github.movementspeed.nhglib.data.models.serialization.physics.shapes

import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.physics.models.SphereRigidBodyShape

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
class SphereShapeJson : ShapeJson() {
    private var shape: SphereRigidBodyShape? = null

    override fun parse(jsonValue: JsonValue) {
        val radius = jsonValue.getFloat("radius")
        shape = SphereRigidBodyShape(radius)
    }

    override fun get(): SphereRigidBodyShape? {
        return shape
    }
}
