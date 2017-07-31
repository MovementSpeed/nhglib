package io.github.voidzombie.nhglib.physics.models;

public class ConvexHullRigidBodyShape extends RigidBodyShape {
    public boolean optimize;
    public String asset;

    public ConvexHullRigidBodyShape(String asset, boolean optimize) {
        this.asset = asset;
        this.optimize = optimize;
    }
}
