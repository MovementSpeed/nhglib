package io.github.movementspeed.nhglib.graphics.lights;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

public class ShadowDirectionalLightProperties extends ShadowLightProperties<PerspectiveCamera, Texture, FrameBuffer> {
    @Override
    public void build(NhgLight light) {
        lightCamera = new PerspectiveCamera(90, 1024, 1024);
        lightCamera.near = 0.1f;
        lightCamera.far = 1;
        lightCamera.position.set(light.position);
        lightCamera.lookAt(Vector3.Zero);
        lightCamera.update();

        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 1024, 1024, true);
    }

    @Override
    public void updateSampler() {
        depthSampler = frameBuffer.getColorBufferTexture();
    }

    @Override
    public void bind(NhgLight light, ShaderProgram shaderProgram) {
        lightCamera.position.set(light.position);
        lightCamera.lookAt(Vector3.Zero);
        lightCamera.update();

        final int textureNum = 3;
        depthSampler.bind(textureNum);
        shaderProgram.setUniformi("u_depthMapDir", textureNum);
        shaderProgram.setUniformMatrix("u_lightMatrix", lightCamera.combined);
        shaderProgram.setUniformf("u_cameraFar", lightCamera.far);
        shaderProgram.setUniformf("u_type", 1);
        shaderProgram.setUniformf("u_lightPosition", light.position);
    }
}
