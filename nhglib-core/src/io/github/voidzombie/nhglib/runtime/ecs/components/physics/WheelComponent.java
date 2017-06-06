package io.github.voidzombie.nhglib.runtime.ecs.components.physics;

import com.artemis.Component;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRaycastVehicle;

/**
 * Created by Fausto Napoli on 05/06/2017.
 */
public class WheelComponent extends Component {
    private int wheelIndex;
    private btRaycastVehicle vehicle;

    public void build(btRaycastVehicle vehicle, int wheelIndex) {
        this.vehicle = vehicle;
        this.wheelIndex = wheelIndex;
    }

    public Vector3 getTranslation() {
        Matrix4 mat = vehicle.getWheelTransformWS(wheelIndex);
        return mat.getTranslation(new Vector3());
    }

    public Quaternion getRotation() {
        Matrix4 mat = vehicle.getWheelTransformWS(wheelIndex);
        return mat.getRotation(new Quaternion());
    }
}