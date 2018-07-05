package io.github.movementspeed.nhglib.graphics.geometry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class NhgModelMaterial {
    public float opacity = 1.f;

    public String id;

    public Color albedo;
    public Color ambient;

    public Array<NhgModelTexture> textures;
}
