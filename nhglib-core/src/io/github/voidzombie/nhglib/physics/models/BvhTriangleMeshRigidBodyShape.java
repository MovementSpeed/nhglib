package io.github.voidzombie.nhglib.physics.models;

public class BvhTriangleMeshRigidBodyShape extends RigidBodyShape {
    public boolean quantization;
    public boolean buildBvh;

    public String asset;

    public BvhTriangleMeshRigidBodyShape(String asset, boolean quantization, boolean buildBvh) {
        this.asset = asset;
        this.quantization = quantization;
        this.buildBvh = buildBvh;
    }
}
