package io.github.voidzombie.nhglib.graphics.lights.forward;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Fausto Napoli on 13/03/2017.
 */
public class NhgDirectionalLight extends DirectionalLight {
    public float intensity = 1.0f;

    public NhgDirectionalLight set(Color color, Vector3 direction, float intensity) {
        this.intensity = intensity;
        super.set(color, direction);

        return this;
    }
}