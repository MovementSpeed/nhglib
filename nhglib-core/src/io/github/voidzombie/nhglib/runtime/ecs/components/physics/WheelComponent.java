package io.github.voidzombie.nhglib.runtime.ecs.components.physics;

import com.artemis.Component;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRaycastVehicle;
import com.badlogic.gdx.physics.bullet.dynamics.btWheelInfo;

/**
 * Created by Fausto Napoli on 05/06/2017.
 */
public class WheelComponent extends Component {
    public boolean frontWheel;
    public int wheelIndex;

    public float radius;
    public float suspensionRestLength;
    public float wheelFriction;

    public State state;

    public Vector3 attachmentPoint;
    public Vector3 direction;
    public Vector3 axis;
    public Vector3 translation;
    public Quaternion rotation;

    public VehicleComponent vehicleComponent;

    public WheelComponent() {
        state = State.NOT_INITIALIZED;
    }

    public void build() {
        translation = new Vector3();
        rotation = new Quaternion();

        vehicleComponent.addWheel(attachmentPoint, direction, axis, radius,
                suspensionRestLength, wheelFriction, frontWheel);
    }

    public float getRotation() {
        btWheelInfo info = vehicleComponent.vehicle.getWheelInfo(wheelIndex);
        return info.getRotation();
    }

    public float getSteering() {
        btWheelInfo info = vehicleComponent.vehicle.getWheelInfo(wheelIndex);
        return info.getSteering();
    }

    public Vector3 getTranslation() {
        Matrix4 mat = vehicleComponent.vehicle.getWheelTransformWS(wheelIndex);
        return mat.getTranslation(translation);
    }

    public Quaternion getRotationQuaternion() {
        Matrix4 mat = vehicleComponent.vehicle.getWheelTransformWS(wheelIndex);
        return mat.getRotation(rotation);
    }

    public btRaycastVehicle getVehicle() {
        return vehicleComponent.vehicle;
    }

    public enum State {
        NOT_INITIALIZED,
        READY
    }
}
