package io.github.movementspeed.nhglib.data.models.serialization.physics.shapes

import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.physics.models.ConvexTriangleMeshRigidBodyShape

/**
 * Created by Fausto Napoli on 15/06/2017.
 */
class ConvexTriangleMeshShapeJson : ShapeJson() {
    private var shape: ConvexTriangleMeshRigidBodyShape? = null

    override fun parse(jsonValue: JsonValue) {
        val calcAabb = jsonValue.getBoolean("calcAabb", true)
        val asset = jsonValue.getString("asset", "")

        shape = ConvexTriangleMeshRigidBodyShape(asset, calcAabb)
    }

    override fun get(): ConvexTriangleMeshRigidBodyShape? {
        return shape
    }
}
