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
    private int wheelIndex;

    private Vector3 translation;
    private Quaternion rotation;
    private btRaycastVehicle vehicle;

    public void build(btRaycastVehicle vehicle, int wheelIndex) {
        this.vehicle = vehicle;
        this.wheelIndex = wheelIndex;

        translation = new Vector3();
        rotation = new Quaternion();
    }

    public Vector3 getTranslation() {
        Matrix4 mat = vehicle.getWheelTransformWS(wheelIndex);
        return mat.getTranslation(translation);
    }

    public Quaternion getRotationQuaternion() {
        Matrix4 mat = vehicle.getWheelTransformWS(wheelIndex);
        return mat.getRotation(rotation);
    }

    public float getRotation() {
        btWheelInfo info = vehicle.getWheelInfo(wheelIndex);
        return info.getRotation();
    }

    public float getSteering() {
        btWheelInfo info = vehicle.getWheelInfo(wheelIndex);
        return info.getSteering();
    }

    public btRaycastVehicle getVehicle() {
        return vehicle;
    }
}
