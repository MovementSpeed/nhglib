package io.github.voidzombie.nhglib.data.models.serialization.physics;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public enum RigidBodyType {
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
                res = BOX;
                break;

            case "sphere":
                res = BOX;
                break;

            case "capsule":
                res = BOX;
                break;

            case "cylinder":
                res = BOX;
                break;

            case "convexHull":
                res = BOX;
                break;

            case "bvhTriangleMesh":
                res = BOX;
                break;
        }

        return res;
    }
}
