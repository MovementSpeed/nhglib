package io.github.movementspeed.nhglib.graphics.shaders.tiled;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.graphics.rendering.RenderPass;

public class TiledPBRRenderPass extends RenderPass {
    public TiledPBRRenderPass() {
        super(false);
    }

    @Override
    public void created() {
        setShaderProvider(new TiledPBRShaderProvider(environment));
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
