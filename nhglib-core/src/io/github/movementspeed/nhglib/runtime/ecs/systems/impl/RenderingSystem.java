package io.github.movementspeed.nhglib.runtime.ecs.systems.impl;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
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
    public static boolean depthTextureMode;

    // Injected references
    private CameraSystem cameraSystem;

    private ShaderProvider shaderProvider;
    private Environment environment;
    private Color clearColor;
    private ModelBatch renderer;
    //private ModelBatch depthBatch;
    //private NhgFrameBuffer nhgFrameBuffer;
    private FPSLogger fpsLogger;
    //private Texture depthTexture;

    //private Array<Camera> cameras;
    //private Array<ModelBatch> modelBatches;
    private Array<RenderingSystemInterface> renderingInterfaces;

    //private SpriteBatch spriteBatch;

    public RenderingSystem() {
        clearColor = Color.BLACK;
        fpsLogger = new FPSLogger();
        environment = new Environment();

        shaderProvider = new PBRShaderProvider(environment);
        renderer = new ModelBatch(shaderProvider);

        /*DepthShaderProvider depthShaderProvider = new DepthShaderProvider();
        depthBatch = new ModelBatch(depthShaderProvider);

        nhgFrameBuffer = new NhgFrameBuffer();
        nhgFrameBuffer.type = NhgFrameBuffer.Type.DEPTH;
        nhgFrameBuffer.width = Gdx.graphics.getBackBufferWidth();
        nhgFrameBuffer.height = Gdx.graphics.getBackBufferHeight();
        nhgFrameBuffer.init();

        modelBatches = new Array<>();*/
        renderingInterfaces = new Array<>();

        //spriteBatch = new SpriteBatch();
    }

    @Override
    protected void processSystem() {
        if (Nhg.debugLogs && Nhg.debugFpsLogs) {
            fpsLogger.log();
        }

        if (cameraSystem.cameras.size > 0) {
            Camera camera = cameraSystem.cameras.first();
            GLUtils.clearScreen(clearColor);

            renderer.begin(camera);
            for (RenderingSystemInterface rsi : renderingInterfaces) {
                renderer.render(rsi.getRenderableProviders(), environment);
                rsi.clearRenderableProviders();
            }
            renderer.end();
        }

        /*if (cameras == null) {
            cameras = cameraSystem.cameras;
        }*/

        // Add as many ModelBatches as needed. If there are 5 cameras,
        // 5 model batches are added. Since there are 5 model batches,
        // cameras.size - modelBatches.size is 0, so none are added.
        // So if a camera is added, this will add just 1 model batch.
        /*for (int i = 0; i < cameras.size - modelBatches.size; i++) {
            modelBatches.add(new ModelBatch(shaderProvider));
        }*/

        /*for (int i = 0; i < cameras.size; i++) {
            Camera camera = cameras.get(i);
            ModelBatch modelBatch = modelBatches.get(i);

            GLUtils.clearScreen(clearColor);

            modelBatch.begin(camera);
            for (RenderingSystemInterface rsi : renderingInterfaces) {
                modelBatch.render(rsi.getRenderableProviders(), environment);
            }
            modelBatch.end();

            if (depthTextureMode) {
                // Update depth texture
                nhgFrameBuffer.begin();
                GLUtils.clearScreen(Color.WHITE);

                depthBatch.begin(camera);
                for (RenderingSystemInterface rsi : renderingInterfaces) {
                    depthBatch.render(rsi.getRenderableProviders(), environment);
                }
                depthBatch.end();
                nhgFrameBuffer.end();

                depthTexture = nhgFrameBuffer.texture;
            }

            if (Nhg.debugLogs && Nhg.debugFpsLogs) {
                fpsLogger.log();
            }

            TextureRegion tr = new TextureRegion(depthTexture);
            tr.flip(false, true);

            spriteBatch.begin();
            spriteBatch.draw(tr, 0, 0);
            spriteBatch.end();
        }*/
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

    @Override
    public void dispose() {
        super.dispose();
        //nhgFrameBuffer.dispose();
        shaderProvider.dispose();
        renderer.dispose();
        //depthBatch.dispose();

        /*cameras.clear();

        if (depthTexture != null) {
            depthTexture.dispose();
        }

        for (ModelBatch mb : modelBatches) {
            mb.dispose();
        }*/
    }
}
