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
    public RenderPass previousRenderPass;

    protected ModelBatch renderer;
    protected FrameBuffer mainFBO;
    protected Environment environment;
    protected ShaderProvider shaderProvider;

    public RenderPass(boolean outputData) {
        if (outputData) {
            bundle = new Bundle();
        }
    }

    public abstract void created();
    public abstract void begin(PerspectiveCamera camera);
    public abstract void render(PerspectiveCamera camera, Array<RenderableProvider> renderableProviders);
    public abstract void end();

    public void setShaderProvider(ShaderProvider shaderProvider) {
        this.shaderProvider = shaderProvider;
        this.renderer = new ModelBatch(shaderProvider);
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

    public Bundle getOutputData() {
        return bundle;
    }
}
