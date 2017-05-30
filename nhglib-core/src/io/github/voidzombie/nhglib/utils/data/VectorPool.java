package io.github.voidzombie.nhglib.utils.data;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import io.github.voidzombie.nhglib.math.Vector4;

/**
 * Created by Fausto Napoli on 23/01/2017.
 */
public class VectorPool {
    private static Pool<Vector2> vector2Pool = new Pool<Vector2>() {
        @Override
        protected Vector2 newObject() {
            return new Vector2(0f, 0f);
        }

        @Override
        protected void reset(Vector2 object) {
            super.reset(object);
            object.set(Vector2.Zero);
        }
    };

    private static Pool<Vector3> vector3Pool = new Pool<Vector3>() {
        @Override
        protected Vector3 newObject() {
            return new Vector3(0f, 0f, 0f);
        }

        @Override
        protected void reset(Vector3 object) {
            super.reset(object);
            object.set(Vector3.Zero);
        }
    };

    private static Pool<Vector4> vector4Pool = new Pool<Vector4>() {
        @Override
        protected Vector4 newObject() {
            return new Vector4(0f, 0f, 0f, 0f);
        }

        @Override
        protected void reset(Vector4 object) {
            super.reset(object);
            object.set(Vector4.Zero);
        }
    };

    public synchronized static void freeVector2(Vector2... vec) {
        for (Vector2 vec2 : vec) {
            vector2Pool.free(vec2);
        }
    }

    public synchronized static void freeVector3(Vector3... vec) {
        for (Vector3 vec3 : vec) {
            vector3Pool.free(vec3);
        }
    }

    public synchronized static void freeVector4(Vector4... vec) {
        for (Vector4 vec4 : vec) {
            vector4Pool.free(vec4);
        }
    }

    public synchronized static Vector2 getVector2() {
        return vector2Pool.obtain();
    }

    public synchronized static Vector2 getRandomVector2(float min, float max) {
        Vector2 vector = vector2Pool.obtain();
        vector.set(
                MathUtils.random(min, max),
                MathUtils.random(min, max));

        return vector;
    }

    public synchronized static Vector3 getVector3() {
        return vector3Pool.obtain();
    }

    public synchronized static Vector3 getRandomVector3(float min, float max) {
        Vector3 vector = vector3Pool.obtain();
        vector.set(
                MathUtils.random(min, max),
                MathUtils.random(min, max),
                MathUtils.random(min, max));

        return vector;
    }

    public synchronized static Vector4 getVector4() {
        return vector4Pool.obtain();
    }

    public synchronized static Vector4 getRandomVector4(float min, float max) {
        Vector4 vector = vector4Pool.obtain();
        vector.set(
                MathUtils.random(min, max),
                MathUtils.random(min, max),
                MathUtils.random(min, max),
                MathUtils.random(min, max));

        return vector;
    }
}
