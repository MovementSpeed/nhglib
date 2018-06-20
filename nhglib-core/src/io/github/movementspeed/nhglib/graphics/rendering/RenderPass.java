package io.github.movementspeed.nhglib.graphics.rendering;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.utils.data.Bundle;

public abstract class RenderPass {
    public Bundle bundle;

    protected ModelBatch renderer;
    protected FrameBuffer mainFBO;
    protected RenderPass previousRenderPass;
    protected Environment environment;

    public RenderPass(boolean outputData, ShaderProvider shaderProvider) {
        if (outputData) {
            bundle = new Bundle();
        }

        this.renderer = new ModelBatch(shaderProvider);
    }

    public abstract void begin(PerspectiveCamera camera);
    public abstract void render(PerspectiveCamera camera, Array<RenderableProvider> renderableProviders);
    public abstract void end();

    public Bundle getOutputData() {
        return bundle;
    }

    public void setMainFBO(FrameBuffer mainFBO) {
        this.mainFBO = mainFBO;
    }

    public void setPreviousRenderPass(RenderPass previousRenderPass) {
        this.previousRenderPass = previousRenderPass;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
