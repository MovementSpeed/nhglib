package io.github.movementspeed.nhglib.graphics.geometry;

import com.badlogic.gdx.math.Vector2;

public class NhgModelTexture {
    public final static int USAGE_UNKNOWN = 0;
    public final static int USAGE_NONE = 1;
    public final static int USAGE_ALBEDO = 2;
    public final static int USAGE_ROUGHNESS = 3;
    public final static int USAGE_METALNESS = 4;
    public final static int USAGE_NORMAL = 5;
    public final static int USAGE_EMISSIVE = 6;
    public final static int USAGE_AMBIENT_OCCLUSION = 7;
    public final static int USAGE_TRANSPARENCY = 8;

    public String id;
    public String fileName;
    public Vector2 uvTranslation;
    public Vector2 uvScaling;
    public int usage;
}
