package io.github.movementspeed.nhglib.graphics.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;

/**
 * Created by Fausto Napoli on 03/04/2017.
 */
public class PBRMaterial extends Material {
    public boolean blended;

    public float metalnessValue;
    public float roughnessValue;
    public float aoValue;

    public float offsetU;
    public float offsetV;
    public float tilesU;
    public float tilesV;

    public String targetNode;

    public String albedo;
    public String normal;
    public String rma;

    public Color albedoColor;
}
