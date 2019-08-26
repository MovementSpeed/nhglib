package io.github.movementspeed.nhglib.physics.enums;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public enum RigidBodyType {
    CONVEX_TRIANGLE_MESH,
    BVH_TRIANGLE_MESH,
    CONVEX_HULL,
    CYLINDER,
    CAPSULE,
    SPHERE,
    CONE,
    BOX;

    public static RigidBodyType fromString(String name) {
        RigidBodyType res = null;

        switch (name) {
            case "box":
                res = BOX;
                break;

            case "cone":
                res = CONE;
                break;

            case "sphere":
                res = SPHERE;
                break;

            case "capsule":
                res = CAPSULE;
                break;

            case "cylinder":
                res = CYLINDER;
                break;

            case "convexHull":
                res = CONVEX_HULL;
                break;

            case "bvhTriangleMesh":
                res = BVH_TRIANGLE_MESH;
                break;

            case "convexTriangleMesh":
                res = CONVEX_TRIANGLE_MESH;
                break;
        }

        return res;
    }
}
