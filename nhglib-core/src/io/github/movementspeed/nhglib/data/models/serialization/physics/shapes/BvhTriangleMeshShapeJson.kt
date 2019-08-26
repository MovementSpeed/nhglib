package io.github.movementspeed.nhglib.data.models.serialization.physics.shapes

import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.physics.models.BvhTriangleMeshRigidBodyShape

/**
 * Created by Fausto Napoli on 15/06/2017.
 */
class BvhTriangleMeshShapeJson : ShapeJson() {
    private var shape: BvhTriangleMeshRigidBodyShape? = null

    override fun parse(jsonValue: JsonValue) {
        val quantization = jsonValue.getBoolean("useQuantizedAabbCompression", true)
        val buildBvh = jsonValue.getBoolean("buildBvh", true)

        val asset = jsonValue.getString("asset", "")

        shape = BvhTriangleMeshRigidBodyShape(asset, quantization, buildBvh)
    }

    override fun get(): BvhTriangleMeshRigidBodyShape? {
        return shape
    }
}
