package io.github.voidzombie.nhglib.physics.models;

public class CapsuleRigidBodyShape extends RigidBodyShape {
    public float radius;
    public float height;

    public CapsuleRigidBodyShape(float radius, float height) {
        this.radius = radius;
        this.height = height;
    }
}
