package io.github.voidzombie.nhglib.graphics.lights;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import io.github.voidzombie.nhglib.enums.LightType;
import io.github.voidzombie.nhglib.utils.data.MatrixPool;
import io.github.voidzombie.nhglib.utils.data.VectorPool;

/**
 * Created by Fausto Napoli on 18/03/2017.
 */
public class NhgLight extends BaseLight<NhgLight> {
    public final Vector3 position;
    public final Vector3 direction;

    public LightType type;
    public float radius;
    public float intensity;
    public float innerAngle;
    public float outerAngle;

    private Matrix4 transform;

    public NhgLight() {
        intensity = 0;
        position = VectorPool.getVector3();
        direction = VectorPool.getVector3();
        transform = MatrixPool.getMatrix4();
    }

    public static NhgLight directional(float intensity, Color color) {
        NhgLight light = new NhgLight();
        light.type = LightType.DIRECTIONAL_LIGHT;
        light.intensity = intensity;
        light.color.set(color);

        return light;
    }

    public static NhgLight point(float intensity, float radius, Color color) {
        NhgLight light = new NhgLight();
        light.type = LightType.POINT_LIGHT;
        light.intensity = intensity;
        light.color.set(color);
        light.radius = radius;

        return light;
    }

    public static NhgLight spot(float intensity, float radius, float innerAngle, float outerAngle, Color color) {
        NhgLight light = new NhgLight();
        light.type = LightType.SPOT_LIGHT;
        light.intensity = intensity;
        light.radius = radius;
        light.color.set(color);
        light.innerAngle = innerAngle;
        light.outerAngle = outerAngle;

        return light;
    }

    public void setTransform(Matrix4 transform) {
        this.transform.set(transform);
    }

    public NhgLight copy() {
        NhgLight copy = new NhgLight();

        copy.position.set(this.position);
        copy.direction.set(this.direction);
        copy.type = this.type;
        copy.radius = this.radius;
        copy.intensity = this.intensity;
        copy.innerAngle = this.innerAngle;
        copy.outerAngle = this.outerAngle;
        copy.transform = this.transform;

        return copy;
    }

    public Matrix4 getTransform() {
        return transform;
    }
}