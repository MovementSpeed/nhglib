package io.github.movementspeed.nhglib.graphics.lights;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import io.github.movementspeed.nhglib.utils.graphics.GLUtils;

public abstract class ShadowLightProperties<CameraType extends Camera, SamplerType extends GLTexture, FrameBufferType extends GLFrameBuffer> {
    public CameraType lightCamera;
    public SamplerType depthSampler;
    public FrameBufferType frameBuffer;

    public abstract void build(NhgLight light);
    public abstract void updateSampler();
    public abstract void bind(NhgLight light, ShaderProgram shaderProgram);

    public void begin() {
        frameBuffer.begin();
        GLUtils.clearScreen(Color.BLACK);
    }

    public void end() {
        frameBuffer.end();
        updateSampler();
    }
}
