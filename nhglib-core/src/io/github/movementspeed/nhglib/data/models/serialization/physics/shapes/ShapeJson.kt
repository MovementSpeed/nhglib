package io.github.movementspeed.nhglib.data.models.serialization.physics.shapes

import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.interfaces.JsonParseable
import io.github.movementspeed.nhglib.physics.enums.RigidBodyType
import io.github.movementspeed.nhglib.physics.models.RigidBodyShape

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
open class ShapeJson : JsonParseable<RigidBodyShape> {
    var output: RigidBodyShape? = null

    override fun parse(shapeJson: JsonValue) {
        val type = RigidBodyType.fromString(shapeJson.getString("type"))

        when (type) {
            RigidBodyType.SPHERE -> {
                val sphereShapeJson = SphereShapeJson()
                sphereShapeJson.parse(shapeJson)
                output = sphereShapeJson.get()
            }

            RigidBodyType.BOX -> {
                val boxShapeJson = BoxShapeJson()
                boxShapeJson.parse(shapeJson)
                output = boxShapeJson.get()
            }

            RigidBodyType.CYLINDER -> {
                val cylinderShapeJson = CylinderShapeJson()
                cylinderShapeJson.parse(shapeJson)
                output = cylinderShapeJson.get()
            }

            RigidBodyType.CONE -> {
                val coneShapeJson = ConeShapeJson()
                coneShapeJson.parse(shapeJson)
                output = coneShapeJson.get()
            }

            RigidBodyType.CAPSULE -> {
                val capsuleShapeJson = CapsuleShapeJson()
                capsuleShapeJson.parse(shapeJson)
                output = capsuleShapeJson.get()
            }

            RigidBodyType.CONVEX_HULL -> {
                val convexHullShapeJson = ConvexHullShapeJson()
                convexHullShapeJson.parse(shapeJson)
                output = convexHullShapeJson.get()
            }

            RigidBodyType.BVH_TRIANGLE_MESH -> {
                val bvhTriangleMeshShapeJson = BvhTriangleMeshShapeJson()
                bvhTriangleMeshShapeJson.parse(shapeJson)
                output = bvhTriangleMeshShapeJson.get()
            }

            RigidBodyType.CONVEX_TRIANGLE_MESH -> {
                val convexTriangleMeshShapeJson = ConvexTriangleMeshShapeJson()
                convexTriangleMeshShapeJson.parse(shapeJson)
                output = convexTriangleMeshShapeJson.get()
            }
        }

        output!!.type = type
    }

    override fun get(): RigidBodyShape {
        return output
    }
}
