package io.github.movementspeed.nhglib.data.models.serialization.physics.shapes

import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.physics.models.ConvexHullRigidBodyShape

/**
 * Created by Fausto Napoli on 15/06/2017.
 */
class ConvexHullShapeJson : ShapeJson() {
    private var shape: ConvexHullRigidBodyShape? = null

    override fun parse(jsonValue: JsonValue) {
        val optimize = jsonValue.getBoolean("optimize", false)
        val asset = jsonValue.getString("asset", "")

        shape = ConvexHullRigidBodyShape(asset, optimize)
    }

    override fun get(): ConvexHullRigidBodyShape? {
        return shape
    }
}
