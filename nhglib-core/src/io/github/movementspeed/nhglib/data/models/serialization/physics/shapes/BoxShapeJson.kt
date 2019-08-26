package io.github.movementspeed.nhglib.data.models.serialization.physics.shapes

import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.physics.models.BoxRigidBodyShape

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
class BoxShapeJson : ShapeJson() {
    private var shape: BoxRigidBodyShape? = null

    override fun parse(jsonValue: JsonValue) {
        val width = jsonValue.getFloat("width", 0.0f) * 0.5f
        val height = jsonValue.getFloat("height", 0.0f) * 0.5f
        val depth = jsonValue.getFloat("depth", 0.0f) * 0.5f

        shape = BoxRigidBodyShape(width, height, depth)
    }

    override fun get(): BoxRigidBodyShape? {
        return shape
    }
}
