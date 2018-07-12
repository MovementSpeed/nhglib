package io.github.movementspeed.nhglib.graphics.lights;

import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBufferCubemap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShadowPointLightProperties extends ShadowLightProperties<PerspectiveCamera, Cubemap, FrameBufferCubemap> {
    @Override
    public void build(NhgLight light) {
        lightCamera = new PerspectiveCamera(90f, 1024, 1024);
        lightCamera.near = 4f;
        lightCamera.far = 70;
        lightCamera.position.set(light.position);
        lightCamera.update();

        frameBuffer = new FrameBufferCubemap(Pixmap.Format.RGBA8888, 1024, 1024, true);
    }

    @Override
    public void updateSampler() {
        depthSampler = frameBuffer.getColorBufferTexture();
    }

    @Override
    public void bind(NhgLight light, ShaderProgram shaderProgram) {
        final int textureNum = 2;
        depthSampler.bind(textureNum);
        shaderProgram.setUniformf("u_type", 2);
        shaderProgram.setUniformi("u_depthMapCube", textureNum);
        shaderProgram.setUniformf("u_cameraFar", lightCamera.far);
        shaderProgram.setUniformf("u_lightPosition", light.position);
    }
}
