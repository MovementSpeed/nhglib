package io.github.voidzombie.nhglib.graphics.utils;

import com.badlogic.gdx.graphics.g3d.Material;

/**
 * Created by Fausto Napoli on 03/04/2017.
 */
public class PbrMaterial extends Material {
    public String targetNode;

    public String albedo;
    public String metalness;
    public String roughness;
    public String normal;
    public String ambientOcclusion;
}
