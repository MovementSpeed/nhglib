package io.github.voidzombie.nhglib.data.models.serialization.physics;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public enum RigidBodyType {
    CYLINDER,
    CAPSULE,
    SPHERE,
    CONE,
    BOX;

    public static RigidBodyType fromString(String name) {
        return RigidBodyType.valueOf(name.toUpperCase());
    }
}
