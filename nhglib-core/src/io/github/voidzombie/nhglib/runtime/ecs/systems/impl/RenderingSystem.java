package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import io.github.voidzombie.nhglib.graphics.shaders.depth.DepthShaderProvider;
import io.github.voidzombie.nhglib.graphics.shaders.tiledForward.TiledForwardShaderProvider;
import io.github.voidzombie.nhglib.runtime.ecs.interfaces.RenderingSystemInterface;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.BaseRenderingSystem;
import io.github.voidzombie.nhglib.utils.graphics.GLUtils;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class RenderingSystem extends BaseSystem implements Disposable {
    // Injected references
    private CameraSystem cameraSystem;

    private ShaderProvider shaderProvider;
    private Environment environment;
    private Color clearColor;

    public Texture depthTexture;
    private ModelBatch depthBatch;
    private FrameBuffer depthFrameBuffer;

    private Array<Camera> cameras;
    private Array<ModelBatch> modelBatches;
    private Array<RenderingSystemInterface> renderingInterfaces;

    public RenderingSystem() {
        clearColor = Color.BLACK;
        environment = new Environment();

        this.shaderProvider = new TiledForwardShaderProvider(environment);

        DepthShaderProvider depthShaderProvider = new DepthShaderProvider();
        depthBatch = new ModelBatch(depthShaderProvider);

        depthFrameBuffer = new FrameBuffer(
                Pixmap.Format.RGB888,
                Gdx.graphics.getBackBufferWidth(),
                Gdx.graphics.getBackBufferHeight(),
                true);

        modelBatches = new Array<>();
        renderingInterfaces = new Array<>();
    }

    @Override
    protected void processSystem() {
        if (cameras == null) {
            cameras = cameraSystem.cameras;
        }

        // Add as many ModelBatches as needed. If there are 5 cameras,
        // 5 model batches are added. Since there are 5 model batches,
        // cameras.size - modelBatches.size is 0, so none are added.
        // So if a camera is added, this will add just 1 model batch.
        for (int i = 0; i < cameras.size - modelBatches.size; i++) {
            modelBatches.add(new ModelBatch(shaderProvider));
        }

        for (int i = 0; i < cameras.size; i++) {
            Camera camera = cameras.get(i);
            ModelBatch modelBatch = modelBatches.get(i);

            GLUtils.clearScreen(clearColor);

            modelBatch.begin(camera);
            for (RenderingSystemInterface rsi : renderingInterfaces) {
                modelBatch.render(rsi.getRenderableProviders(), environment);
            }
            modelBatch.end();

            // Update depth texture
            depthFrameBuffer.begin();
            GLUtils.clearScreen(Color.WHITE);

            depthBatch.begin(camera);
            for (RenderingSystemInterface rsi : renderingInterfaces) {
                depthBatch.render(rsi.getRenderableProviders(), environment);
            }
            depthBatch.end();
            depthFrameBuffer.end();

            depthTexture = depthFrameBuffer.getColorBufferTexture();
        }
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

    public Environment getEnvironment() {
        return environment;
    }

    public Texture getDepthTexture() {
        return depthTexture;
    }

    @Override
    public void dispose() {
        super.dispose();
        depthFrameBuffer.dispose();
        shaderProvider.dispose();
        depthTexture.dispose();
        depthBatch.dispose();

        cameras.clear();

        for (ModelBatch mb : modelBatches) {
            mb.dispose();
        }
    }
}
