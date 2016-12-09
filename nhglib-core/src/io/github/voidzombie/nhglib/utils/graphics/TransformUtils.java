package io.github.voidzombie.nhglib.utils.graphics;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class TransformUtils {
    public final static Vector3 ZERO_VECTOR_3;
    public final static Vector3 ONE_VECTOR_3;
    public final static Quaternion ZERO_QUATERNION;

    static {
        ZERO_VECTOR_3 = Vector3.Zero;
        ONE_VECTOR_3 = new Vector3(1, 1, 1);
        ZERO_QUATERNION = new Quaternion().setEulerAngles(0, 0, 0);
    }
}
