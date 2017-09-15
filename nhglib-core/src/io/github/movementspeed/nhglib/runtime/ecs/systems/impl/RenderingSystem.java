package io.github.movementspeed.nhglib.runtime.ecs.systems.impl;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.graphics.shaders.tiledForward.PBRShaderProvider;
import io.github.movementspeed.nhglib.runtime.ecs.interfaces.RenderingSystemInterface;
import io.github.movementspeed.nhglib.runtime.ecs.systems.base.BaseRenderingSystem;
import io.github.movementspeed.nhglib.utils.graphics.GLUtils;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class RenderingSystem extends BaseSystem implements Disposable {
    // Injected references
    private CameraSystem cameraSystem;

    private ShaderProvider shaderProvider;
    private Environment environment;
    private Color clearColor;
    private ModelBatch renderer;
    private FPSLogger fpsLogger;
    private FrameBuffer frameBuffer;

    private Array<RenderingSystemInterface> renderingInterfaces;

    private SpriteBatch spriteBatch;

    public RenderingSystem() {
        clearColor = Color.BLACK;
        fpsLogger = new FPSLogger();
        environment = new Environment();

        shaderProvider = new PBRShaderProvider(environment);
        renderer = new ModelBatch(shaderProvider);

        renderingInterfaces = new Array<>();

        spriteBatch = new SpriteBatch();
        updateFramebuffer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    protected void processSystem() {
        if (Nhg.debugLogs && Nhg.debugFpsLogs) {
            fpsLogger.log();
        }

        if (cameraSystem.cameras.size > 0) {
            Camera camera = cameraSystem.cameras.first();

            frameBuffer.begin();
            Gdx.gl.glViewport(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
            GLUtils.clearScreen(clearColor);

            renderer.begin(camera);
            for (RenderingSystemInterface rsi : renderingInterfaces) {
                renderer.render(rsi.getRenderableProviders(), environment);
                rsi.clearRenderableProviders();
            }
            renderer.end();
            frameBuffer.end();

            spriteBatch.begin();
            spriteBatch.draw(frameBuffer.getColorBufferTexture(),
                    0, 0,
                    Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                    0, 0, 1, 1);
            spriteBatch.end();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        shaderProvider.dispose();
        renderer.dispose();
        frameBuffer.dispose();
    }

    public void setRenderScale(float renderScale) {
        if (renderScale < 0f) renderScale = 0f;
        if (renderScale > 1f) renderScale = 1f;

        updateFramebuffer(Math.round(Gdx.graphics.getWidth() * renderScale), Math.round(Gdx.graphics.getHeight() * renderScale));
    }

    public void setClearColor(Color clearColor) {
        if (clearColor != null) {
            this.clearColor = clearColor;
        }
    }

    public void addRenderingInterfaces(BaseRenderingSystem... renderingSystems) {
        for (BaseRenderingSystem brs : renderingSystems) {
            renderingInterfaces.add(brs);
        }
    }

    public void addRenderingInterfaces(RenderingSystemInterface... renderingSystemInterfaces) {
        for (RenderingSystemInterface rsi : renderingSystemInterfaces) {
            renderingInterfaces.add(rsi);
        }
    }

    public Environment getEnvironment() {
        return environment;
    }

    public Texture getDepthTexture() {
        return null;
    }

    private void updateFramebuffer(int width, int height) {
        if (frameBuffer != null) {
            frameBuffer.dispose();
        }

        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
    }
}
