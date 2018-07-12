package io.github.movementspeed.nhglib.graphics.shaders.shadows;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.graphics.rendering.RenderPass;

public class ShadowsDepthRenderPass extends RenderPass {
    public ShadowsDepthRenderPass() {
        super(false);
    }

    @Override
    public void created() {
        setShaderProvider(new DepthShaderProvider());
    }

    @Override
    public void begin(PerspectiveCamera camera) {
    }

    @Override
    public void render(PerspectiveCamera camera, Array<RenderableProvider> renderableProviders) {

    }

    @Override
    public void end() {
        mainFBO.begin();
    }
}
