package io.github.movementspeed.nhglib.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

/**
 * Created by Fausto Napoli on 04/05/2017.
 */
public class MotionState extends btMotionState {
    public Matrix4 transform;

    public MotionState() {
        transform = new Matrix4();
    }

    @Override
    public void getWorldTransform(Matrix4 worldTrans) {
        worldTrans.set(transform);
    }

    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
        transform.set(worldTrans);
    }
}
