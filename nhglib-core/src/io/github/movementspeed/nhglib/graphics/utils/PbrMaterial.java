package io.github.movementspeed.nhglib.graphics.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;

/**
 * Created by Fausto Napoli on 03/04/2017.
 */
public class PbrMaterial extends Material {
    public float metalnessValue;
    public float roughnessValue;

    public String targetNode;

    public String albedo;
    public String metalness;
    public String roughness;
    public String normal;
    public String ambientOcclusion;

    public Color albedoColor;
}
