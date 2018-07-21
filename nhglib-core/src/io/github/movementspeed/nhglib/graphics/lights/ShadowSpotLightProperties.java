package io.github.movementspeed.nhglib.graphics.lights;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShadowSpotLightProperties extends ShadowLightProperties<PerspectiveCamera, Texture, FrameBuffer> {
    @Override
    public void build(NhgLight light) {
        lightCamera = new PerspectiveCamera(light.outerAngle, 2048, 2048);
        lightCamera.near = 1f;
        lightCamera.far = 10;
        lightCamera.transform(light.getTransform());
        lightCamera.update();

        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 2048, 2048, true);
    }

    @Override
    public void updateSampler() {
        depthSampler = frameBuffer.getColorBufferTexture();
    }

    @Override
    public void bind(NhgLight light, ShaderProgram shaderProgram) {
        lightCamera.transform(light.getTransform());
        lightCamera.update();

        final int textureNum = 3;
        depthSampler.bind(textureNum);
        shaderProgram.setUniformi("u_depthMapDir", textureNum);
        shaderProgram.setUniformf("u_type", 1);
        shaderProgram.setUniformf("u_cameraFar", lightCamera.far);
        shaderProgram.setUniformf("u_lightPosition", light.position);
    }
}
