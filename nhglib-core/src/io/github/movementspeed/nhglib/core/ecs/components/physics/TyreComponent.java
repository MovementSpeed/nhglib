package io.github.movementspeed.nhglib.core.ecs.components.physics;

import com.artemis.Component;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRaycastVehicle;
import com.badlogic.gdx.physics.bullet.dynamics.btWheelInfo;

/**
 * Created by Fausto Napoli on 05/06/2017.
 */
public class TyreComponent extends Component {
    public boolean frontTyre;
    public int index;

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

    public TyreComponent() {
        state = State.NOT_INITIALIZED;
    }

    public void build() {
        translation = new Vector3();
        rotation = new Quaternion();

        vehicleComponent.addTyre(attachmentPoint, direction, axis, radius,
                suspensionRestLength, wheelFriction, frontTyre);
    }

    public float getRotation() {
        btWheelInfo info = vehicleComponent.vehicle.getWheelInfo(index);
        return info.getRotation();
    }

    public float getSteering() {
        btWheelInfo info = vehicleComponent.vehicle.getWheelInfo(index);
        return info.getSteering();
    }

    public Vector3 getTranslation() {
        Matrix4 mat = vehicleComponent.vehicle.getWheelTransformWS(index);
        return mat.getTranslation(translation);
    }

    public Quaternion getRotationQuaternion() {
        Matrix4 mat = vehicleComponent.vehicle.getWheelTransformWS(index);
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
