package io.github.movementspeed.nhglib.graphics.lights;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GLTexture;

public class ShadowLightProperties<CameraType extends Camera, SamplerType extends GLTexture> {
    public int id;

    public float halfDepth;
    public float halfHeight;

    public CameraType lightView;
    public SamplerType depthSampler;
}
