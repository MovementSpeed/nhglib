package io.github.movementspeed.nhglib.core.ecs.systems.impl;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.core.ecs.interfaces.RenderingSystemInterface;
import io.github.movementspeed.nhglib.core.ecs.systems.base.BaseRenderingSystem;
import io.github.movementspeed.nhglib.graphics.rendering.RenderPass;
import io.github.movementspeed.nhglib.graphics.shaders.tiled.TiledPBRRenderPass;
import io.github.movementspeed.nhglib.utils.graphics.GLUtils;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class RenderingSystem extends BaseSystem implements Disposable {
    public static int renderWidth;
    public static int renderHeight;
    public static Texture depthTexture;

    private boolean saveScreenMode;

    // Injected references
    private CameraSystem cameraSystem;

    private Environment environment;
    private Color clearColor;
    private FPSLogger fpsLogger;
    private FrameBuffer frameBuffer;
    private SpriteBatch spriteBatch;

    private Pixmap screenPixmap;

    private Array<RenderPass> renderPasses;
    private Array<RenderableProvider> renderableProviders;
    private Array<RenderingSystemInterface> renderingInterfaces;

    public RenderingSystem() {
        clearColor = Color.BLACK;
        fpsLogger = new FPSLogger();
        environment = new Environment();

        renderPasses = new Array<>();
        renderableProviders = new Array<>();
        renderingInterfaces = new Array<>();

        spriteBatch = new SpriteBatch();
        spriteBatch.enableBlending();

        updateFramebuffer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //setRenderPass(0, new DepthPreRenderPass());
        //setRenderPass(1, new MinMaxRenderPass());
        setRenderPass(0, new TiledPBRRenderPass());
    }

    @Override
    protected void processSystem() {
        if (Nhg.debugLogs && Nhg.debugFpsLogs) {
            fpsLogger.log();
        }

        if (cameraSystem.cameras.size > 0) {
            PerspectiveCamera camera = (PerspectiveCamera) cameraSystem.cameras.first();

            for (RenderingSystemInterface rsi : renderingInterfaces) {
                rsi.onPreRender();
            }

            renderableProviders.clear();

            for (RenderingSystemInterface rsi : renderingInterfaces) {
                Array<RenderableProvider> providers = rsi.getRenderableProviders();
                renderableProviders.addAll(providers);
                rsi.clearRenderableProviders();
            }

            if (renderableProviders.size > 0) {
                frameBuffer.begin();
                GLUtils.clearScreen(clearColor);

                for (int i = 0; i < renderPasses.size; i++) {
                    RenderPass renderPass = renderPasses.get(i);

                    if (i > 0) {
                        renderPass.setPreviousRenderPass(renderPasses.get(i - 1));
                    }

                    renderPass.begin(camera);
                    renderPass.render(camera, renderableProviders);
                    renderPass.end();
                }

                if (saveScreenMode) {
                    screenPixmap = getScreenPixmapInternal(frameBuffer.getWidth(), frameBuffer.getHeight());
                    saveScreenMode = false;
                }

                frameBuffer.end();

                spriteBatch.begin();
                spriteBatch.draw(frameBuffer.getColorBufferTexture(),
                        0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                        0, 0, 1, 1);
                spriteBatch.end();

                for (RenderingSystemInterface rsi : renderingInterfaces) {
                    rsi.onPostRender();
                }
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        frameBuffer.dispose();
    }

    public void beginScreenPixmap() {
        saveScreenMode = true;
    }

    public Pixmap getScreenPixmap() {
        return screenPixmap;
    }

    public void endScreenPixmap() {
        screenPixmap.dispose();
        screenPixmap = null;
    }

    public void setRenderPass(int index, RenderPass renderPass) {
        if (index >= 0) {
            if (index >= renderPasses.size) {
                renderPasses.setSize(index + 1);
            }

            renderPass.setEnvironment(environment);
            renderPass.setMainFBO(frameBuffer);

            if (index > 0) {
                renderPass.setPreviousRenderPass(renderPasses.get(index - 1));
            }

            renderPass.created();
            renderPasses.set(index, renderPass);
        }
    }

    public void setRenderScale(float renderScale) {
        if (renderScale < 0f) renderScale = 0f;
        if (renderScale > 1f) renderScale = 1f;

        renderWidth = Math.round(Gdx.graphics.getWidth() * renderScale);
        renderHeight = Math.round(Gdx.graphics.getHeight() * renderScale);

        updateFramebuffer(renderWidth, renderHeight);
    }

    public void setRenderResolution(int renderWidth, int renderHeight) {
        if (renderWidth < 1) renderWidth = 1;
        if (renderHeight < 1) renderHeight = 1;

        RenderingSystem.renderWidth = renderWidth;
        RenderingSystem.renderHeight = renderHeight;

        updateFramebuffer(renderWidth, renderHeight);
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

        try {
            frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
        } catch (IllegalStateException e) {
            frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, width, height, true);
        }

        for (RenderingSystemInterface rsi : renderingInterfaces) {
            rsi.onUpdatedRenderer(width, height);
        }
    }

    private Pixmap getScreenPixmapInternal(int width, int height) {
        byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, width, height, true);

        // this loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
        for(int i = 4; i < pixels.length; i += 4) {
            pixels[i - 1] = (byte) 255;
        }

        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        return pixmap;
    }
}
