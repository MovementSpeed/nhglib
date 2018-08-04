package io.github.movementspeed.nhglib.graphics.lights;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import io.github.movementspeed.nhglib.graphics.utils.NhgFrameBufferCubemap;
import io.github.movementspeed.nhglib.utils.graphics.GLUtils;

public class ShadowPointLightProperties extends ShadowLightProperties<PerspectiveCamera, Cubemap, NhgFrameBufferCubemap> {
    private int cubeSide = 0;

    @Override
    public void build(NhgLight light) {
        lightCamera = new PerspectiveCamera(90f, 1024, 1024);
        lightCamera.near = 0.1f;
        lightCamera.far = 5f;
        lightCamera.position.set(light.position);
        lightCamera.update();

        frameBuffer = new NhgFrameBufferCubemap(Pixmap.Format.RGBA8888, 1024, 1024, true);
    }

    @Override
    public void updateSampler() {
        depthSampler = frameBuffer.getColorBufferTexture();
    }

    @Override
    public void bind(NhgLight light, ShaderProgram shaderProgram) {
        lightCamera.position.set(light.position);
        lightCamera.update();

        final int textureNum = 2;
        depthSampler.bind(textureNum);
        shaderProgram.setUniformf("u_type", 2);
        shaderProgram.setUniformi("u_depthMapCube", textureNum);
        shaderProgram.setUniformf("u_cameraFar", lightCamera.far);
        shaderProgram.setUniformf("u_lightPosition", light.position);
    }

    @Override
    public void begin() {
        Cubemap.CubemapSide side = Cubemap.CubemapSide.values()[cubeSide];
        frameBuffer.begin();
        frameBuffer.bindSide(side, lightCamera);
        GLUtils.clearScreen(Color.BLACK);
        cubeSide++;
    }

    @Override
    public void end() {
        super.end();
        cubeSide = 0;
    }
}
