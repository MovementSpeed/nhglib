package io.github.voidzombie.nhglib.physics.models;

public class BoxRigidBodyShape extends RigidBodyShape {
    public float width, height, depth;

    public BoxRigidBodyShape(float width, float height, float depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }
}
