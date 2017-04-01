package io.github.voidzombie.nhglib.graphics.lights;

import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.math.Vector3;
import io.github.voidzombie.nhglib.utils.data.VectorPool;

/**
 * Created by Fausto Napoli on 18/03/2017.
 */
public class NhgLight extends BaseLight<NhgLight> {
    public final Vector3 position;
    public final Vector3 direction;

    public float radius;
    public float intensity;
    public float innerAngle;
    public float outerAngle;

    public NhgLight() {
        intensity = 0;
        position = VectorPool.getVector3();
        direction = VectorPool.getVector3();
    }
}