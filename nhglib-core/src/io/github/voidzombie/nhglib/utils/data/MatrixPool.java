package io.github.voidzombie.nhglib.utils.data;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Fausto Napoli on 23/01/2017.
 */
public class MatrixPool {
    private static Pool<Matrix4> matrix4Pool = new Pool<Matrix4>() {
        @Override
        protected Matrix4 newObject() {
            return new Matrix4();
        }

        @Override
        protected void reset(Matrix4 object) {
            super.reset(object);
            object.idt();
        }
    };

    public synchronized static void freeMatrix4(Matrix4... matrix4s) {
        for (Matrix4 matrix4 : matrix4s) {
            matrix4Pool.free(matrix4);
        }
    }

    public synchronized static Matrix4 getMatrix4() {
        return matrix4Pool.obtain();
    }
}
