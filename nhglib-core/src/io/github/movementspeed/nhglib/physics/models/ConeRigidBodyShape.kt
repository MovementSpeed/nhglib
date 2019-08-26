package io.github.movementspeed.nhglib.physics.models;

public class ConeRigidBodyShape extends RigidBodyShape {
    public float radius;
    public float height;

    public ConeRigidBodyShape(float radius, float height) {
        this.radius = radius;
        this.height = height;
    }
}
