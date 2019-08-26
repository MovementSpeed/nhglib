package io.github.movementspeed.nhglib.physics.enums

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
enum class RigidBodyType {
    CONVEX_TRIANGLE_MESH,
    BVH_TRIANGLE_MESH,
    CONVEX_HULL,
    CYLINDER,
    CAPSULE,
    SPHERE,
    CONE,
    BOX;

    companion object {

        fun fromString(name: String): RigidBodyType? {
            var res: RigidBodyType? = null

            when (name) {
                "box" -> res = BOX
                "cone" -> res = CONE
                "sphere" -> res = SPHERE
                "capsule" -> res = CAPSULE
                "cylinder" -> res = CYLINDER
                "convexHull" -> res = CONVEX_HULL
                "bvhTriangleMesh" -> res = BVH_TRIANGLE_MESH
                "convexTriangleMesh" -> res = CONVEX_TRIANGLE_MESH
            }

            return res
        }
    }
}
