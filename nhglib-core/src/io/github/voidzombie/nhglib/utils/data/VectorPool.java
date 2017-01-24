package io.github.voidzombie.nhglib.utils.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Fausto Napoli on 23/01/2017.
 */
public class VectorPool {
    private static Pool<Vector2> vector2Pool = new Pool<Vector2>() {
        @Override
        protected Vector2 newObject() {
            return new Vector2(0f, 0f);
        }
    };

    public static Vector2 getVector2() {
        return vector2Pool.obtain();
    }
}
