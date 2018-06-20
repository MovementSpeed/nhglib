package io.github.movementspeed.nhglib.graphics.shaders.forward;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.graphics.rendering.RenderPass;

public class PBRRenderPass extends RenderPass {
    public PBRRenderPass() {
        super(false, new PBRShaderProvider());
    }

    @Override
    public void begin(PerspectiveCamera camera) {
        renderer.begin(camera);
    }

    @Override
    public void render(PerspectiveCamera camera, Array<RenderableProvider> renderableProviders) {
        renderer.render(renderableProviders, environment);
    }

    @Override
    public void end() {
        renderer.end();
    }
}
