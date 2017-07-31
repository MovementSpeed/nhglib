package io.github.voidzombie.nhglib.physics.models;

public class ConvexTriangleMeshRigidBodyShape extends RigidBodyShape {
    public boolean calcAabb;
    public String asset;

    public ConvexTriangleMeshRigidBodyShape(String asset, boolean calcAabb) {
        this.asset = asset;
        this.calcAabb = calcAabb;
    }
}
