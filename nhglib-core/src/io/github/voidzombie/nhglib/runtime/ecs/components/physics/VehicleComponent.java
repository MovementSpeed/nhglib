package io.github.voidzombie.nhglib.runtime.ecs.components.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDefaultVehicleRaycaster;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRaycastVehicle;
import com.badlogic.gdx.physics.bullet.dynamics.btVehicleRaycaster;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Fausto Napoli on 11/06/2017.
 */
public class VehicleComponent extends RigidBodyComponent implements Disposable {
    private int wheelNumber;

    public btRaycastVehicle.btVehicleTuning vehicleTuning;
    public btVehicleRaycaster vehicleRaycaster;
    public btRaycastVehicle vehicle;

    @Override
    public void addToWorld(btDynamicsWorld world, Matrix4 transform) {
        world.addVehicle(vehicle);
        super.addToWorld(world, transform);
    }

    @Override
    public void dispose() {
        vehicle.dispose();
        vehicleTuning.dispose();
        vehicleRaycaster.dispose();
    }

    public void setSteeringValue(float steering, int wheel) {
        vehicle.setSteeringValue(steering, wheel);
    }

    public void applyEngineForce(float force, int wheel) {
        vehicle.applyEngineForce(force, wheel);
    }

    public void setBrake(float brake, int wheelIndex) {
        vehicle.setBrake(brake, wheelIndex);
    }

    public int getWheelNumber() {
        return wheelNumber;
    }

    public VehicleComponent build(btDynamicsWorld world, btCollisionShape chassisShape, float mass) {
        return build(world, chassisShape, new btRaycastVehicle.btVehicleTuning(), mass);
    }

    public VehicleComponent build(btDynamicsWorld world, btCollisionShape chassisShape,
                                  btRaycastVehicle.btVehicleTuning vehicleTuning, float mass) {
        return build(world, chassisShape, vehicleTuning, mass, 1f, 0f, (short) -1, new short[]{});
    }

    public VehicleComponent build(btDynamicsWorld world, btCollisionShape chassisShape,
                                  btRaycastVehicle.btVehicleTuning vehicleTuning, float mass, float friction, float restitution, short group, short[] masks) {
        this.vehicleTuning = vehicleTuning;

        build(chassisShape, mass, friction, restitution, group, masks);
        getBody().setActivationState(Collision.DISABLE_DEACTIVATION);

        vehicleRaycaster = new btDefaultVehicleRaycaster(world);

        vehicle = new btRaycastVehicle(vehicleTuning, getBody(), vehicleRaycaster);
        vehicle.setCoordinateSystem(0, 1, 2);

        return this;
    }

    public VehicleComponent addWheel(Vector3 attachPoint, float radius, boolean frontWheel) {
        return addWheel(attachPoint, new Vector3(0, -1, 0), new Vector3(-1, 0, 0), radius,
                radius * 0.3f, 10f, frontWheel);
    }

    public VehicleComponent addWheel(Vector3 attachPoint, Vector3 direction, Vector3 axis, float radius,
                                     float suspensionRestLength, float friction, boolean frontWheel) {
        wheelNumber++;

        vehicle.addWheel(attachPoint, direction, axis, suspensionRestLength, radius, vehicleTuning, frontWheel)
                .setFrictionSlip(friction);

        return this;
    }

    public btRaycastVehicle getVehicle() {
        return vehicle;
    }
}
