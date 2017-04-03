package io.github.voidzombie.nhglib.graphics.utils;

import com.badlogic.gdx.graphics.g3d.Material;
import io.github.voidzombie.nhglib.assets.Asset;

/**
 * Created by Fausto Napoli on 03/04/2017.
 */
public class PbrMaterial extends Material {
    public String targetNode;

    public Asset albedoAsset;
    public Asset metalnessAsset;
    public Asset roughnessAsset;
    public Asset normalAsset;
    public Asset ambientOcclusionAsset;
}
