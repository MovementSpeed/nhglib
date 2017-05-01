package io.github.voidzombie.nhglib.utils.data;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Fausto Napoli on 23/01/2017.
 */
public class QuaternionPool {
    private static Pool<Quaternion> quaternionPool = new Pool<Quaternion>() {
        @Override
        protected Quaternion newObject() {
            return new Quaternion();
        }

        @Override
        protected void reset(Quaternion object) {
            super.reset(object);
            object.idt();
        }
    };

    public static void freeQuaternion(Quaternion... quaternions) {
        for (Quaternion q : quaternions) {
            quaternionPool.free(q);
        }
    }

    public static Quaternion getQuaternion() {
        return quaternionPool.obtain();
    }
}
