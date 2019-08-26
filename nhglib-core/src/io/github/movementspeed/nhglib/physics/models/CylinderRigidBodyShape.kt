package io.github.movementspeed.nhglib.physics.models;

public class CylinderRigidBodyShape extends RigidBodyShape {
    public float width, height, depth;

    public CylinderRigidBodyShape(float width, float height, float depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }
}
